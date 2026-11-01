/**
 * Front Desk Check-in / Check-out Controller
 */

async function loadFrontDeskData() {
  try {
    const stats = await api.get('/api/dashboard/stats');
    const allReservations = await api.get('/api/reservations', { status: 'CHECKED_IN' });

    renderDeskArrivals(stats.expectedArrivals || []);
    renderDeskDepartures(stats.expectedDepartures || []);
    renderInHouseGuests(allReservations || []);
  } catch (err) {
    showToast('Failed to load front desk records: ' + err.message, 'error');
  }
}

function renderDeskArrivals(arrivals) {
  const tbody = document.getElementById('desk-arrivals-tbody');
  document.getElementById('desk-arrivals-count').textContent = arrivals.length;

  if (!arrivals || arrivals.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">No arrivals scheduled for today.</td></tr>`;
    return;
  }

  tbody.innerHTML = arrivals.map(r => `
    <tr>
      <td><strong>${escapeHtml(r.bookingReference)}</strong></td>
      <td>
        <div><strong>${escapeHtml(r.guestName)}</strong></div>
        <small style="color: var(--text-muted);">${escapeHtml(r.guestPhone || '')}</small>
      </td>
      <td><strong>Room ${escapeHtml(r.roomNumber)}</strong></td>
      <td>${r.numberOfNights} night${r.numberOfNights > 1 ? 's' : ''}</td>
      <td style="text-align: right;">
        <button class="btn btn-primary btn-sm" onclick="deskCheckIn(${r.id}, '${escapeHtml(r.bookingReference)}')">
          Check In
        </button>
      </td>
    </tr>
  `).join('');
}

function renderDeskDepartures(departures) {
  const tbody = document.getElementById('desk-departures-tbody');
  document.getElementById('desk-departures-count').textContent = departures.length;

  if (!departures || departures.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">No departures scheduled for today.</td></tr>`;
    return;
  }

  tbody.innerHTML = departures.map(r => `
    <tr>
      <td><strong>${escapeHtml(r.bookingReference)}</strong></td>
      <td>
        <div><strong>${escapeHtml(r.guestName)}</strong></div>
        <small style="color: var(--text-muted);">${escapeHtml(r.guestPhone || '')}</small>
      </td>
      <td><strong>Room ${escapeHtml(r.roomNumber)}</strong></td>
      <td><strong>${formatCurrency(r.totalAmount)}</strong></td>
      <td style="text-align: right;">
        <button class="btn btn-danger btn-sm" onclick="deskCheckOut(${r.id}, '${escapeHtml(r.bookingReference)}')">
          Check Out
        </button>
      </td>
    </tr>
  `).join('');
}

function renderInHouseGuests(reservations) {
  const tbody = document.getElementById('in-house-tbody');
  document.getElementById('in-house-count').textContent = `${reservations.length} Active Stay${reservations.length !== 1 ? 's' : ''}`;

  if (!reservations || reservations.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state">No guests currently checked into rooms.</td></tr>`;
    return;
  }

  tbody.innerHTML = reservations.map(r => `
    <tr>
      <td><strong>${escapeHtml(r.bookingReference)}</strong></td>
      <td>
        <div><strong>${escapeHtml(r.guestName)}</strong></div>
        <small style="color: var(--text-muted);">${escapeHtml(r.guestEmail || '')} • ${escapeHtml(r.guestPhone || '')}</small>
      </td>
      <td>
        <strong style="color: var(--primary);">Room ${escapeHtml(r.roomNumber)}</strong>
        <span style="font-size: 11px; color: var(--text-muted);">(${escapeHtml(r.roomType)})</span>
      </td>
      <td>${formatDate(r.checkInDate)}</td>
      <td>${formatDate(r.checkOutDate)}</td>
      <td>${r.billStatus ? getStatusBadge(r.billStatus) : '<span class="badge badge-unpaid">UNPAID</span>'}</td>
      <td style="text-align: right; white-space: nowrap;">
        <a href="billing.html?reservationId=${r.id}" class="btn btn-secondary btn-sm">Manage Folio</a>
        <button class="btn btn-danger btn-sm" onclick="deskCheckOut(${r.id}, '${escapeHtml(r.bookingReference)}')">
          Check Out
        </button>
      </td>
    </tr>
  `).join('');
}

async function deskCheckIn(id, ref) {
  if (!confirm(`Confirm guest arrival and check-in for reservation ${ref}? Room status will become OCCUPIED.`)) return;
  try {
    await api.post(`/api/reservations/${id}/check-in`);
    showToast(`Reservation ${ref} successfully checked in!`, 'success');
    loadFrontDeskData();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function deskCheckOut(id, ref) {
  if (!confirm(`Confirm guest check-out for ${ref}? Please ensure all incidental charges are settled.`)) return;
  try {
    await api.post(`/api/reservations/${id}/check-out`);
    showToast(`Reservation ${ref} checked out successfully. Room released for housekeeping!`, 'success');
    loadFrontDeskData();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  loadFrontDeskData();
});

