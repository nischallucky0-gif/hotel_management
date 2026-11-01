/**
 * Reservations Page Controller
 */

let availableRoomsList = [];
let allGuestsList = [];

async function loadReservations() {
  const tbody = document.getElementById('reservations-table-tbody');
  tbody.innerHTML = `<tr><td colspan="9" class="empty-state"><div class="loading-spinner"></div> Loading reservations...</td></tr>`;

  try {
    const query = document.getElementById('res-search-input').value;
    const status = document.getElementById('res-status-filter').value;
    const fromDate = document.getElementById('res-from-date').value;
    const toDate = document.getElementById('res-to-date').value;

    const reservations = await api.get('/api/reservations', { query, status, fromDate, toDate });
    renderReservationsTable(reservations);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="9" class="empty-state" style="color: var(--danger);">Failed to load reservations: ${escapeHtml(err.message)}</td></tr>`;
    showToast(err.message, 'error');
  }
}

function renderReservationsTable(reservations) {
  const tbody = document.getElementById('reservations-table-tbody');
  document.getElementById('res-count').textContent = reservations.length;

  if (!reservations || reservations.length === 0) {
    tbody.innerHTML = `<tr><td colspan="9" class="empty-state">No reservations found matching criteria.</td></tr>`;
    return;
  }

  tbody.innerHTML = reservations.map(r => `
    <tr>
      <td><strong>${escapeHtml(r.bookingReference)}</strong></td>
      <td>
        <div><strong>${escapeHtml(r.guestName)}</strong></div>
        <small style="color: var(--text-muted);">${escapeHtml(r.guestPhone || '')}</small>
      </td>
      <td>
        <strong>Room ${escapeHtml(r.roomNumber)}</strong>
        <div><small style="color: var(--text-muted);">${escapeHtml(r.roomType)}</small></div>
      </td>
      <td>
        <div>${formatDate(r.checkInDate)} → ${formatDate(r.checkOutDate)}</div>
        <small style="color: var(--primary); font-weight: 600;">${r.numberOfNights} night${r.numberOfNights > 1 ? 's' : ''}</small>
      </td>
      <td>👥 ${r.numberOfGuests}</td>
      <td><strong>${formatCurrency(r.totalAmount)}</strong></td>
      <td>${getStatusBadge(r.status)}</td>
      <td>${r.billStatus ? getStatusBadge(r.billStatus) : '<span style="color: var(--text-muted);">—</span>'}</td>
      <td style="text-align: right; white-space: nowrap;">
        ${r.status === 'CONFIRMED' ? `
          <button class="btn btn-primary btn-sm" onclick="performCheckIn(${r.id}, '${escapeHtml(r.bookingReference)}')">Check In</button>
          <button class="btn btn-danger btn-sm" onclick="cancelReservation(${r.id}, '${escapeHtml(r.bookingReference)}')">Cancel</button>
        ` : ''}
        ${r.status === 'CHECKED_IN' ? `
          <button class="btn btn-danger btn-sm" onclick="performCheckOut(${r.id}, '${escapeHtml(r.bookingReference)}')">Check Out</button>
        ` : ''}
        <a href="billing.html?reservationId=${r.id}" class="btn btn-outline btn-sm">Bill</a>
      </td>
    </tr>
  `).join('');
}

let resDebounceTimeout;
function handleResFilter() {
  clearTimeout(resDebounceTimeout);
  resDebounceTimeout = setTimeout(loadReservations, 250);
}

function resetResFilters() {
  document.getElementById('res-search-input').value = '';
  document.getElementById('res-status-filter').value = '';
  document.getElementById('res-from-date').value = '';
  document.getElementById('res-to-date').value = '';
  loadReservations();
}

async function preloadFormData() {
  try {
    const [rooms, guests] = await Promise.all([
      api.get('/api/rooms'),
      api.get('/api/guests')
    ]);

    availableRoomsList = rooms;
    allGuestsList = guests;

    // Populate Guests dropdown
    const guestSelect = document.getElementById('res-guest-id');
    guestSelect.innerHTML = '<option value="">-- Choose registered guest --</option>' +
      guests.map(g => `<option value="${g.id}">${escapeHtml(g.fullName)} (${escapeHtml(g.email)})</option>`).join('');

    // Populate Rooms dropdown
    const roomSelect = document.getElementById('res-room-id');
    roomSelect.innerHTML = '<option value="">-- Choose Room --</option>' +
      rooms.map(r => `
        <option value="${r.id}" data-price="${r.pricePerNight}" data-capacity="${r.maxOccupancy}" ${r.status === 'MAINTENANCE' ? 'disabled' : ''}>
          Room ${escapeHtml(r.roomNumber)} - ${escapeHtml(r.type)} (${formatCurrency(r.pricePerNight)}/nt, max ${r.maxOccupancy}) ${r.status !== 'AVAILABLE' ? `[${r.status}]` : ''}
        </option>
      `).join('');

  } catch (err) {
    console.error('Failed to preload dropdown data:', err);
  }
}

function toggleGuestMode() {
  const isNew = document.querySelector('input[name="guestMode"]:checked').value === 'new';
  document.getElementById('existing-guest-section').style.display = isNew ? 'none' : 'block';
  document.getElementById('new-guest-section').style.display = isNew ? 'block' : 'none';
}

function calculateReservationPrice() {
  const roomSelect = document.getElementById('res-room-id');
  const selectedOption = roomSelect.options[roomSelect.selectedIndex];
  const checkInVal = document.getElementById('res-checkin').value;
  const checkOutVal = document.getElementById('res-checkout').value;
  const detailsEl = document.getElementById('rate-calculation-details');
  const displayEl = document.getElementById('calculated-total-display');

  if (!selectedOption || !selectedOption.value || !checkInVal || !checkOutVal) {
    detailsEl.textContent = 'Select room and check-in/out dates to see calculated rate.';
    displayEl.textContent = '$0.00';
    return;
  }

  const pricePerNight = parseFloat(selectedOption.getAttribute('data-price')) || 0;
  const inDate = new Date(checkInVal);
  const outDate = new Date(checkOutVal);

  if (outDate <= inDate) {
    detailsEl.textContent = '⚠️ Check-out date must be strictly after check-in date.';
    displayEl.textContent = '$0.00';
    return;
  }

  const diffTime = outDate.getTime() - inDate.getTime();
  const nights = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  const total = nights * pricePerNight;

  detailsEl.innerHTML = `<strong>${nights} night${nights > 1 ? 's' : ''}</strong> &times; ${formatCurrency(pricePerNight)} / night`;
  displayEl.textContent = formatCurrency(total);
}

function openReservationModal() {
  const modal = document.getElementById('reservation-modal');
  preloadFormData();

  // Set default check-in to today, check-out to tomorrow
  const today = new Date().toISOString().split('T')[0];
  const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];
  document.getElementById('res-checkin').value = today;
  document.getElementById('res-checkout').value = tomorrow;

  modal.classList.add('show');
}

function closeReservationModal() {
  document.getElementById('reservation-modal').classList.remove('show');
  document.getElementById('reservation-form').reset();
  toggleGuestMode();
  document.getElementById('calculated-total-display').textContent = '$0.00';
}

async function saveReservation(event) {
  event.preventDefault();
  const isNewGuest = document.querySelector('input[name="guestMode"]:checked').value === 'new';

  const payload = {
    roomId: parseInt(document.getElementById('res-room-id').value, 10),
    numberOfGuests: parseInt(document.getElementById('res-num-guests').value, 10),
    checkInDate: document.getElementById('res-checkin').value,
    checkOutDate: document.getElementById('res-checkout').value,
    notes: document.getElementById('res-notes').value.trim()
  };

  if (isNewGuest) {
    payload.guestFullName = document.getElementById('new-guest-name').value.trim();
    payload.guestEmail = document.getElementById('new-guest-email').value.trim();
    payload.guestPhoneNumber = document.getElementById('new-guest-phone').value.trim();
    payload.guestIdReference = document.getElementById('new-guest-idref').value.trim();
  } else {
    const guestIdVal = document.getElementById('res-guest-id').value;
    if (!guestIdVal) {
      showToast('Please select an existing guest from the list', 'warning');
      return;
    }
    payload.guestId = parseInt(guestIdVal, 10);
  }

  const btn = document.getElementById('btn-save-res');
  btn.disabled = true;
  btn.textContent = 'Reserving...';

  try {
    const res = await api.post('/api/reservations', payload);
    showToast(`Reservation ${res.bookingReference} confirmed successfully! Total: ${formatCurrency(res.totalAmount)}`, 'success');
    closeReservationModal();
    loadReservations();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Confirm & Book Room';
  }
}

async function performCheckIn(id, ref) {
  if (!confirm(`Check in guest for reservation ${ref}? Room status will be updated to OCCUPIED.`)) return;
  try {
    await api.post(`/api/reservations/${id}/check-in`);
    showToast(`Reservation ${ref} checked in successfully`, 'success');
    loadReservations();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function performCheckOut(id, ref) {
  if (!confirm(`Check out guest for reservation ${ref}? Room status will become AVAILABLE.`)) return;
  try {
    await api.post(`/api/reservations/${id}/check-out`);
    showToast(`Reservation ${ref} checked out successfully`, 'success');
    loadReservations();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function cancelReservation(id, ref) {
  if (!confirm(`Are you sure you want to cancel reservation ${ref}? This will free up the room.`)) return;
  try {
    await api.patch(`/api/reservations/${id}/cancel`);
    showToast(`Reservation ${ref} cancelled`, 'success');
    loadReservations();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  loadReservations();
});

