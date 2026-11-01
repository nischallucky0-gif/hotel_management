/**
 * Guest Directory Controller
 */

let allGuests = [];

async function loadGuests() {
  const tbody = document.getElementById('guests-table-tbody');
  tbody.innerHTML = `<tr><td colspan="6" class="empty-state"><div class="loading-spinner"></div> Loading guests...</td></tr>`;

  try {
    const query = document.getElementById('guest-search-input').value;
    const guests = await api.get('/api/guests', { query });
    allGuests = guests;
    renderGuestsTable(guests);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" class="empty-state" style="color: var(--danger);">Failed to load guests: ${escapeHtml(err.message)}</td></tr>`;
    showToast(err.message, 'error');
  }
}

function renderGuestsTable(guests) {
  const tbody = document.getElementById('guests-table-tbody');
  document.getElementById('guests-count').textContent = guests.length;

  if (!guests || guests.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" class="empty-state">No guests registered yet.</td></tr>`;
    return;
  }

  tbody.innerHTML = guests.map(guest => `
    <tr>
      <td><strong>${escapeHtml(guest.fullName)}</strong></td>
      <td><a href="mailto:${escapeHtml(guest.email)}" style="color: var(--primary); text-decoration: none;">${escapeHtml(guest.email)}</a></td>
      <td>${escapeHtml(guest.phoneNumber)}</td>
      <td><code>${escapeHtml(guest.idReference || '—')}</code></td>
      <td style="max-width: 220px; color: var(--text-muted); font-size: 13px;">${escapeHtml(guest.address || '—')}</td>
      <td style="text-align: right; white-space: nowrap;">
        <button class="btn btn-secondary btn-sm" onclick="editGuest(${guest.id})">Edit</button>
        <button class="btn btn-primary btn-sm" onclick="viewGuestHistory(${guest.id}, '${escapeHtml(guest.fullName)}')">History</button>
      </td>
    </tr>
  `).join('');
}

let guestSearchTimeout;
function handleGuestSearch() {
  clearTimeout(guestSearchTimeout);
  guestSearchTimeout = setTimeout(loadGuests, 250);
}

function resetGuestSearch() {
  document.getElementById('guest-search-input').value = '';
  loadGuests();
}

function openGuestModal(isEdit = false) {
  const modal = document.getElementById('guest-modal');
  document.getElementById('guest-modal-title').textContent = isEdit ? 'Edit Guest Profile' : 'Register New Guest';
  modal.classList.add('show');
}

function closeGuestModal() {
  const modal = document.getElementById('guest-modal');
  modal.classList.remove('show');
  document.getElementById('guest-form').reset();
  document.getElementById('guest-id').value = '';
}

function editGuest(id) {
  const guest = allGuests.find(g => g.id === id);
  if (!guest) return;

  document.getElementById('guest-id').value = guest.id;
  document.getElementById('guest-name').value = guest.fullName;
  document.getElementById('guest-email').value = guest.email;
  document.getElementById('guest-phone').value = guest.phoneNumber;
  document.getElementById('guest-id-ref').value = guest.idReference || '';
  document.getElementById('guest-address').value = guest.address || '';

  openGuestModal(true);
}

async function saveGuest(event) {
  event.preventDefault();
  const id = document.getElementById('guest-id').value;
  const isEdit = Boolean(id);

  const payload = {
    fullName: document.getElementById('guest-name').value.trim(),
    email: document.getElementById('guest-email').value.trim(),
    phoneNumber: document.getElementById('guest-phone').value.trim(),
    idReference: document.getElementById('guest-id-ref').value.trim(),
    address: document.getElementById('guest-address').value.trim()
  };

  const btn = document.getElementById('btn-save-guest');
  btn.disabled = true;
  btn.textContent = 'Saving...';

  try {
    if (isEdit) {
      await api.put(`/api/guests/${id}`, payload);
      showToast(`Guest profile updated successfully`, 'success');
    } else {
      await api.post('/api/guests', payload);
      showToast(`Guest registered successfully`, 'success');
    }
    closeGuestModal();
    loadGuests();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Save Guest';
  }
}

async function viewGuestHistory(guestId, guestName) {
  const modal = document.getElementById('history-modal');
  document.getElementById('history-modal-title').textContent = `Stay History: ${guestName}`;
  const infoEl = document.getElementById('history-guest-info');
  const tbody = document.getElementById('history-table-tbody');
  
  infoEl.innerHTML = `<strong>Guest ID:</strong> #${guestId} &nbsp;|&nbsp; <strong>Name:</strong> ${escapeHtml(guestName)}`;
  tbody.innerHTML = `<tr><td colspan="6" class="empty-state"><div class="loading-spinner"></div> Loading stay history...</td></tr>`;
  modal.classList.add('show');

  try {
    const reservations = await api.get(`/api/guests/${guestId}/reservations`);
    if (!reservations || reservations.length === 0) {
      tbody.innerHTML = `<tr><td colspan="6" class="empty-state">No reservations recorded for this guest.</td></tr>`;
      return;
    }

    tbody.innerHTML = reservations.map(r => `
      <tr>
        <td><strong>${escapeHtml(r.bookingReference)}</strong></td>
        <td>Room ${escapeHtml(r.roomNumber)} (${escapeHtml(r.roomType)})</td>
        <td>${formatDate(r.checkInDate)}</td>
        <td>${formatDate(r.checkOutDate)}</td>
        <td><strong>${formatCurrency(r.totalAmount)}</strong></td>
        <td>${getStatusBadge(r.status)}</td>
      </tr>
    `).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" class="empty-state" style="color: var(--danger);">Failed to load history: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function closeHistoryModal() {
  document.getElementById('history-modal').classList.remove('show');
}

document.addEventListener('DOMContentLoaded', () => {
  loadGuests();
});

