/**
 * Billing & Payments Controller
 */

let allBills = [];

async function loadBills() {
  const tbody = document.getElementById('bills-table-tbody');
  tbody.innerHTML = `<tr><td colspan="11" class="empty-state"><div class="loading-spinner"></div> Loading guest bills...</td></tr>`;

  try {
    const urlParams = new URLSearchParams(window.location.search);
    const reservationIdParam = urlParams.get('reservationId');

    const query = document.getElementById('bill-search-input').value;
    const status = document.getElementById('bill-status-filter').value;

    let bills = await api.get('/api/bills', { query, status });

    if (reservationIdParam) {
      bills = bills.filter(b => String(b.reservationId) === String(reservationIdParam));
    }

    allBills = bills;
    renderBillsTable(bills);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="11" class="empty-state" style="color: var(--danger);">Failed to load bills: ${escapeHtml(err.message)}</td></tr>`;
    showToast(err.message, 'error');
  }
}

function renderBillsTable(bills) {
  const tbody = document.getElementById('bills-table-tbody');
  document.getElementById('bills-count').textContent = bills.length;

  if (!bills || bills.length === 0) {
    tbody.innerHTML = `<tr><td colspan="11" class="empty-state">No bills found matching criteria.</td></tr>`;
    return;
  }

  tbody.innerHTML = bills.map(b => `
    <tr>
      <td><strong>${escapeHtml(b.billNumber)}</strong></td>
      <td><code>${escapeHtml(b.bookingReference)}</code></td>
      <td>
        <div><strong>${escapeHtml(b.guestName)}</strong></div>
        <small style="color: var(--text-muted);">${escapeHtml(b.guestPhone || '')}</small>
      </td>
      <td>Room ${escapeHtml(b.roomNumber)}</td>
      <td>${formatCurrency(b.roomCharges)}</td>
      <td>${formatCurrency(b.additionalChargesTotal)}</td>
      <td><strong>${formatCurrency(b.totalAmount)}</strong></td>
      <td style="color: var(--success); font-weight: 600;">${formatCurrency(b.paidAmount)}</td>
      <td style="color: ${b.balanceAmount > 0 ? 'var(--danger)' : 'var(--text-muted)'}; font-weight: 700;">
        ${formatCurrency(b.balanceAmount)}
      </td>
      <td>${getStatusBadge(b.status)}</td>
      <td style="text-align: right; white-space: nowrap;">
        <button class="btn btn-secondary btn-sm" onclick="openChargeModal(${b.id})">+ Extra</button>
        ${b.balanceAmount > 0 ? `
          <button class="btn btn-success btn-sm" onclick="openPaymentModal(${b.id})">Pay</button>
        ` : ''}
        <button class="btn btn-outline btn-sm" onclick="openInvoiceModal(${b.id})">Invoice</button>
      </td>
    </tr>
  `).join('');
}

let billDebounceTimeout;
function handleBillFilter() {
  clearTimeout(billDebounceTimeout);
  billDebounceTimeout = setTimeout(loadBills, 250);
}

function resetBillFilters() {
  document.getElementById('bill-search-input').value = '';
  document.getElementById('bill-status-filter').value = '';
  if (window.location.search) {
    window.history.replaceState({}, document.title, window.location.pathname);
  }
  loadBills();
}

function openChargeModal(billId) {
  const bill = allBills.find(b => b.id === billId);
  if (!bill) return;

  document.getElementById('charge-bill-id').value = bill.id;
  document.getElementById('charge-bill-meta').innerHTML = `
    <strong>Invoice:</strong> ${escapeHtml(bill.billNumber)} &nbsp;|&nbsp; 
    <strong>Guest:</strong> ${escapeHtml(bill.guestName)} &nbsp;|&nbsp; 
    <strong>Room:</strong> ${escapeHtml(bill.roomNumber)}
  `;

  document.getElementById('charge-form').reset();
  document.getElementById('charge-modal').classList.add('show');
}

function closeChargeModal() {
  document.getElementById('charge-modal').classList.remove('show');
}

async function saveAdditionalCharge(event) {
  event.preventDefault();
  const billId = document.getElementById('charge-bill-id').value;
  const payload = {
    serviceCategory: document.getElementById('charge-category').value,
    amount: parseFloat(document.getElementById('charge-amount').value),
    description: document.getElementById('charge-desc').value.trim()
  };

  const btn = document.getElementById('btn-save-charge');
  btn.disabled = true;
  btn.textContent = 'Posting...';

  try {
    await api.post(`/api/bills/${billId}/charges`, payload);
    showToast(`Added ${payload.serviceCategory} charge of ${formatCurrency(payload.amount)}`, 'success');
    closeChargeModal();
    loadBills();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Post Charge to Folio';
  }
}

function openPaymentModal(billId) {
  const bill = allBills.find(b => b.id === billId);
  if (!bill) return;

  document.getElementById('payment-bill-id').value = bill.id;
  document.getElementById('payment-bill-meta').innerHTML = `
    <strong>Invoice:</strong> ${escapeHtml(bill.billNumber)} &nbsp;|&nbsp; 
    <strong>Guest:</strong> ${escapeHtml(bill.guestName)} &nbsp;|&nbsp; 
    <strong>Outstanding Balance:</strong> <span style="font-size: 15px; font-weight: 800; color: var(--danger);">${formatCurrency(bill.balanceAmount)}</span>
  `;

  document.getElementById('payment-amount').value = bill.balanceAmount;
  document.getElementById('payment-amount').max = bill.balanceAmount;
  document.getElementById('payment-balance-hint').textContent = `Max payable balance: ${formatCurrency(bill.balanceAmount)}`;

  document.getElementById('payment-modal').classList.add('show');
}

function closePaymentModal() {
  document.getElementById('payment-modal').classList.remove('show');
}

async function savePayment(event) {
  event.preventDefault();
  const billId = document.getElementById('payment-bill-id').value;
  const payload = {
    amount: parseFloat(document.getElementById('payment-amount').value),
    paymentMethod: document.getElementById('payment-method').value,
    notes: document.getElementById('payment-notes').value.trim()
  };

  const btn = document.getElementById('btn-save-payment');
  btn.disabled = true;
  btn.textContent = 'Recording...';

  try {
    await api.post(`/api/bills/${billId}/payments`, payload);
    showToast(`Payment of ${formatCurrency(payload.amount)} via ${payload.paymentMethod} recorded successfully`, 'success');
    closePaymentModal();
    loadBills();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Record Payment';
  }
}

async function openInvoiceModal(billId) {
  const modal = document.getElementById('invoice-modal');
  const printArea = document.getElementById('invoice-print-content');
  printArea.innerHTML = `<div class="empty-state"><div class="loading-spinner"></div> Generating invoice preview...</div>`;
  modal.classList.add('show');

  try {
    const bill = await api.get(`/api/bills/${billId}`);
    renderInvoiceHtml(bill, printArea);
  } catch (err) {
    printArea.innerHTML = `<div class="empty-state" style="color: var(--danger);">Failed to load invoice: ${escapeHtml(err.message)}</div>`;
  }
}

function closeInvoiceModal() {
  document.getElementById('invoice-modal').classList.remove('show');
}

function renderInvoiceHtml(bill, container) {
  const chargesList = bill.additionalCharges || [];
  const paymentsList = bill.payments || [];

  container.innerHTML = `
    <div class="invoice-container">
      <div class="invoice-header">
        <div class="hotel-brand">
          <h2>GRAND HORIZON HOTEL &amp; RESORT</h2>
          <p style="font-size: 13px; color: var(--text-muted); margin-top: 4px;">
            100 Ocean Drive, Palm Coast • +1 (800) 555-0199<br>
            reservations@grandhorizon.hotel • Tax ID: HST-9920148
          </p>
        </div>
        <div class="invoice-meta">
          <h3 style="font-size: 20px; color: var(--text-main);">TAX INVOICE / FOLIO</h3>
          <p style="font-size: 14px; font-weight: 700; margin-top: 4px;">${escapeHtml(bill.billNumber)}</p>
          <p style="font-size: 12px; color: var(--text-muted);">Date: ${formatDateTime(bill.createdAt)}</p>
          <div style="margin-top: 6px;">${getStatusBadge(bill.status)}</div>
        </div>
      </div>

      <div class="invoice-parties">
        <div>
          <div style="font-size: 11px; text-transform: uppercase; color: var(--text-muted); font-weight: 700;">Billed To:</div>
          <div style="font-size: 16px; font-weight: 700; color: var(--text-main); margin-top: 2px;">${escapeHtml(bill.guestName)}</div>
          <div style="font-size: 13px; color: var(--text-muted); margin-top: 2px;">Email: ${escapeHtml(bill.guestEmail || '—')}</div>
          <div style="font-size: 13px; color: var(--text-muted);">Phone: ${escapeHtml(bill.guestPhone || '—')}</div>
        </div>
        <div style="text-align: right;">
          <div style="font-size: 11px; text-transform: uppercase; color: var(--text-muted); font-weight: 700;">Stay Details:</div>
          <div style="font-size: 14px; font-weight: 600; color: var(--text-main); margin-top: 2px;">Booking Ref: ${escapeHtml(bill.bookingReference)}</div>
          <div style="font-size: 13px; color: var(--text-muted);">Room: <strong>${escapeHtml(bill.roomNumber)}</strong> (${escapeHtml(bill.roomType)})</div>
          <div style="font-size: 13px; color: var(--text-muted);">Period: ${formatDate(bill.checkInDate)} → ${formatDate(bill.checkOutDate)} (${bill.numberOfNights} nights)</div>
        </div>
      </div>

      <!-- Line Items Table -->
      <table class="invoice-table">
        <thead>
          <tr>
            <th>Description</th>
            <th>Department / Category</th>
            <th style="text-align: center;">Qty / Nights</th>
            <th style="text-align: right;">Rate</th>
            <th style="text-align: right;">Amount</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><strong>Room Accommodation</strong></td>
            <td>Room Revenue</td>
            <td style="text-align: center;">${bill.numberOfNights}</td>
            <td style="text-align: right;">${formatCurrency(bill.roomPricePerNight)}</td>
            <td style="text-align: right;"><strong>${formatCurrency(bill.roomCharges)}</strong></td>
          </tr>
          ${chargesList.map(c => `
            <tr>
              <td>${escapeHtml(c.description || c.serviceCategory)}</td>
              <td>${escapeHtml(c.serviceCategory)}</td>
              <td style="text-align: center;">1</td>
              <td style="text-align: right;">${formatCurrency(c.amount)}</td>
              <td style="text-align: right;"><strong>${formatCurrency(c.amount)}</strong></td>
            </tr>
          `).join('')}
        </tbody>
      </table>

      <!-- Totals & Balances -->
      <div class="invoice-totals">
        <div class="invoice-totals-row">
          <span>Room Charges Subtotal:</span>
          <span>${formatCurrency(bill.roomCharges)}</span>
        </div>
        <div class="invoice-totals-row">
          <span>Incidental Services:</span>
          <span>${formatCurrency(bill.additionalChargesTotal)}</span>
        </div>
        <div class="invoice-totals-row grand-total">
          <span>Total Payable:</span>
          <span>${formatCurrency(bill.totalAmount)}</span>
        </div>
        <div class="invoice-totals-row" style="color: var(--success); font-weight: 600;">
          <span>Payments Received:</span>
          <span>- ${formatCurrency(bill.paidAmount)}</span>
        </div>
        <div class="invoice-totals-row" style="font-size: 16px; font-weight: 800; color: ${bill.balanceAmount > 0 ? 'var(--danger)' : 'var(--text-main)'}; border-top: 1px dashed var(--border); padding-top: 8px;">
          <span>Balance Outstanding:</span>
          <span>${formatCurrency(bill.balanceAmount)}</span>
        </div>
      </div>

      <!-- Payment Receipts Breakdown -->
      ${paymentsList.length > 0 ? `
        <div style="margin-top: 24px; padding-top: 16px; border-top: 1px solid var(--border);">
          <div style="font-size: 12px; font-weight: 700; text-transform: uppercase; color: var(--text-muted); margin-bottom: 8px;">
            Recorded Payment Settlements (Simulated Transactions)
          </div>
          <table style="width: 100%; font-size: 12px; border-collapse: collapse;">
            <tbody>
              ${paymentsList.map(p => `
                <tr style="border-bottom: 1px dashed #e2e8f0;">
                  <td style="padding: 6px 0;"><strong>${escapeHtml(p.transactionReference)}</strong></td>
                  <td style="padding: 6px 0;"><span class="badge" style="background:#e0f2fe; color:#0369a1;">${escapeHtml(p.paymentMethod)}</span></td>
                  <td style="padding: 6px 0; color: var(--text-muted);">${formatDateTime(p.paymentDate)}</td>
                  <td style="padding: 6px 0; color: var(--text-muted);">${escapeHtml(p.notes || '')}</td>
                  <td style="padding: 6px 0; text-align: right; font-weight: 700; color: var(--success);">${formatCurrency(p.amount)}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      ` : ''}

      <div style="margin-top: 30px; padding: 14px; background: #f8fafc; border-radius: var(--radius-sm); text-align: center; font-size: 12px; color: var(--text-muted); border: 1px solid var(--border);">
        Thank you for choosing Grand Horizon Hotel. We look forward to welcoming you again!
      </div>
    </div>
  `;
}

document.addEventListener('DOMContentLoaded', () => {
  loadBills();
});

