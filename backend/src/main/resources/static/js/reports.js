/**
 * Reports & Analytics Controller
 */

function setQuickDateRange(days) {
  const to = new Date();
  const from = new Date(Date.now() - days * 86400000);
  document.getElementById('report-from-date').value = from.toISOString().split('T')[0];
  document.getElementById('report-to-date').value = to.toISOString().split('T')[0];
  loadAllReports();
}

async function loadAllReports() {
  const fromDate = document.getElementById('report-from-date').value;
  const toDate = document.getElementById('report-to-date').value;

  try {
    const [revReport, occReport, bookings] = await Promise.all([
      api.get('/api/reports/revenue', { fromDate, toDate }),
      api.get('/api/reports/occupancy'),
      api.get('/api/reports/bookings', { fromDate, toDate })
    ]);

    renderRevenueReport(revReport);
    renderOccupancyReport(occReport);
    renderBookingsReport(bookings);
  } catch (err) {
    showToast('Failed to load reports: ' + err.message, 'error');
  }
}

function renderRevenueReport(report) {
  document.getElementById('rep-total-collected').textContent = formatCurrency(report.totalRevenue);
  document.getElementById('rep-total-billed').textContent = formatCurrency(report.totalBilled);
  document.getElementById('rep-total-outstanding').textContent = formatCurrency(report.totalOutstanding);
  document.getElementById('rep-bills-count').textContent = report.totalBillsCount;

  // Breakdown by payment method
  const breakdownGrid = document.getElementById('payment-method-breakdown-grid');
  const methods = report.revenueByPaymentMethod || {};
  breakdownGrid.innerHTML = Object.entries(methods).map(([method, amount]) => `
    <div style="background: #f8fafc; padding: 14px 18px; border-radius: var(--radius-sm); border: 1px solid var(--border);">
      <div style="font-size: 12px; font-weight: 700; color: var(--text-muted); text-transform: uppercase;">${escapeHtml(method)}</div>
      <div style="font-size: 20px; font-weight: 800; color: var(--text-main); margin-top: 4px;">${formatCurrency(amount)}</div>
    </div>
  `).join('');

  // Recent payments
  const tbody = document.getElementById('rep-payments-tbody');
  const payments = report.recentPayments || [];
  if (payments.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">No payment transactions recorded for this period.</td></tr>`;
    return;
  }

  tbody.innerHTML = payments.map(p => `
    <tr>
      <td><strong>${escapeHtml(p.transactionReference)}</strong></td>
      <td><span class="badge" style="background:#e0f2fe; color:#0369a1;">${escapeHtml(p.paymentMethod)}</span></td>
      <td><strong style="color: var(--success);">${formatCurrency(p.amount)}</strong></td>
      <td>${formatDateTime(p.paymentDate)}</td>
      <td style="color: var(--text-muted);">${escapeHtml(p.notes || '—')}</td>
    </tr>
  `).join('');
}

function renderOccupancyReport(report) {
  const container = document.getElementById('occupancy-breakdown-container');
  const roomsByType = report.roomsByType || {};
  const occupiedByType = report.occupiedByType || {};

  container.innerHTML = `
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; margin-bottom: 24px;">
      <div style="background: #eff6ff; padding: 16px; border-radius: var(--radius-sm); border: 1px solid #bfdbfe;">
        <div style="font-size: 13px; color: #1e40af; font-weight: 600;">Overall Occupancy Rate</div>
        <div style="font-size: 32px; font-weight: 800; color: #1e40af; margin-top: 4px;">${report.occupancyPercentage}%</div>
        <div style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">${report.occupiedRooms} of ${report.totalRooms} rooms currently occupied</div>
      </div>
      <div style="background: #ecfdf5; padding: 16px; border-radius: var(--radius-sm); border: 1px solid #a7f3d0;">
        <div style="font-size: 13px; color: #065f46; font-weight: 600;">Available for Booking</div>
        <div style="font-size: 32px; font-weight: 800; color: #065f46; margin-top: 4px;">${report.availableRooms}</div>
        <div style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">Ready for immediate guest check-in</div>
      </div>
      <div style="background: #fffbeb; padding: 16px; border-radius: var(--radius-sm); border: 1px solid #fde68a;">
        <div style="font-size: 13px; color: #92400e; font-weight: 600;">Out of Service / Maintenance</div>
        <div style="font-size: 32px; font-weight: 800; color: #92400e; margin-top: 4px;">${report.maintenanceRooms}</div>
        <div style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">Undergoing maintenance or cleaning</div>
      </div>
    </div>

    <h4 style="font-size: 14px; font-weight: 700; margin-bottom: 12px;">Occupancy Breakdown by Room Category</h4>
    <div style="display: flex; flex-direction: column; gap: 12px;">
      ${Object.keys(roomsByType).map(type => {
        const total = roomsByType[type] || 0;
        const occ = occupiedByType[type] || 0;
        const pct = total > 0 ? Math.round((occ / total) * 100) : 0;
        return `
          <div>
            <div style="display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 4px;">
              <span><strong>${escapeHtml(type)}</strong> (${occ} / ${total} occupied)</span>
              <span><strong>${pct}%</strong></span>
            </div>
            <div style="width: 100%; height: 8px; background: #e2e8f0; border-radius: 4px; overflow: hidden;">
              <div style="width: ${pct}%; height: 100%; background: #2563eb; border-radius: 4px;"></div>
            </div>
          </div>
        `;
      }).join('')}
    </div>
  `;
}

function renderBookingsReport(bookings) {
  const tbody = document.getElementById('rep-bookings-tbody');
  document.getElementById('rep-bookings-count').textContent = bookings.length;

  if (!bookings || bookings.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8" class="empty-state">No reservations found in selected period.</td></tr>`;
    return;
  }

  tbody.innerHTML = bookings.map(r => `
    <tr>
      <td><strong>${escapeHtml(r.bookingReference)}</strong></td>
      <td>${escapeHtml(r.guestName)}</td>
      <td>Room ${escapeHtml(r.roomNumber)} (${escapeHtml(r.roomType)})</td>
      <td>${formatDate(r.checkInDate)}</td>
      <td>${formatDate(r.checkOutDate)}</td>
      <td>${r.numberOfNights} night${r.numberOfNights > 1 ? 's' : ''}</td>
      <td><strong>${formatCurrency(r.totalAmount)}</strong></td>
      <td>${getStatusBadge(r.status)}</td>
    </tr>
  `).join('');
}

document.addEventListener('DOMContentLoaded', () => {
  setQuickDateRange(30);
});

