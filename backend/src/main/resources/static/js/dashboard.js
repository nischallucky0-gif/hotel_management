/**
 * Dashboard Controller
 */

async function loadDashboardData() {
  try {
    const stats = await api.get('/api/dashboard/stats');
    
    // Update metric cards
    document.getElementById('stat-total-rooms').textContent = stats.totalRooms;
    document.getElementById('stat-available-rooms').textContent = stats.availableRooms;
    document.getElementById('stat-occupied-rooms').textContent = stats.occupiedRooms;
    document.getElementById('stat-maintenance-rooms').textContent = stats.maintenanceRooms;
    document.getElementById('stat-today-checkins').textContent = stats.todayCheckIns;
    document.getElementById('stat-today-checkouts').textContent = stats.todayCheckOuts;
    document.getElementById('stat-total-reservations').textContent = stats.totalReservations;
    document.getElementById('stat-total-revenue').textContent = formatCurrency(stats.totalRevenue);

    // Update Occupancy indicator
    const rate = stats.occupancyRate || 0;
    document.getElementById('occupancy-percentage').textContent = `${rate}%`;
    document.getElementById('occupancy-progress-fill').style.width = `${Math.min(rate, 100)}%`;
    document.getElementById('legend-avail').textContent = stats.availableRooms;
    document.getElementById('legend-occ').textContent = stats.occupiedRooms;
    document.getElementById('legend-maint').textContent = stats.maintenanceRooms;

    // Render Recent Reservations
    renderRecentReservations(stats.recentReservations || []);

    // Render Expected Arrivals
    renderExpectedArrivals(stats.expectedArrivals || []);

    // Render Expected Departures
    renderExpectedDepartures(stats.expectedDepartures || []);

  } catch (err) {
    showToast('Failed to load dashboard statistics: ' + err.message, 'error');
  }
}

function renderRecentReservations(reservations) {
  const tbody = document.getElementById('recent-reservations-tbody');
  if (!reservations || reservations.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" class="empty-state">No reservations recorded yet.</td></tr>`;
    return;
  }

  tbody.innerHTML = reservations.map(r => `
    <tr>
      <td><strong>${escapeHtml(r.bookingReference)}</strong></td>
      <td>${escapeHtml(r.guestName)}</td>
      <td>Room ${escapeHtml(r.roomNumber)} <span style="font-size: 11px; color: var(--text-muted);">(${escapeHtml(r.roomType)})</span></td>
      <td>${formatDate(r.checkInDate)} → ${formatDate(r.checkOutDate)}</td>
      <td><strong>${formatCurrency(r.totalAmount)}</strong></td>
      <td>${getStatusBadge(r.status)}</td>
    </tr>
  `).join('');
}

function renderExpectedArrivals(arrivals) {
  const tbody = document.getElementById('expected-arrivals-tbody');
  document.getElementById('badge-arrivals-count').textContent = arrivals.length;

  if (!arrivals || arrivals.length === 0) {
    tbody.innerHTML = `<tr><td colspan="4" class="empty-state">No check-ins pending for today.</td></tr>`;
    return;
  }

  tbody.innerHTML = arrivals.map(r => `
    <tr>
      <td><strong>${escapeHtml(r.bookingReference)}</strong></td>
      <td>
        <div>${escapeHtml(r.guestName)}</div>
        <small style="color: var(--text-muted);">${escapeHtml(r.guestPhone)}</small>
      </td>
      <td>Room ${escapeHtml(r.roomNumber)}</td>
      <td>
        <button class="btn btn-primary btn-sm" onclick="performQuickCheckIn(${r.id}, '${escapeHtml(r.bookingReference)}')">
          Check In
        </button>
      </td>
    </tr>
  `).join('');
}

function renderExpectedDepartures(departures) {
  const tbody = document.getElementById('expected-departures-tbody');
  document.getElementById('badge-departures-count').textContent = departures.length;

  if (!departures || departures.length === 0) {
    tbody.innerHTML = `<tr><td colspan="4" class="empty-state">No departures expected today.</td></tr>`;
    return;
  }

  tbody.innerHTML = departures.map(r => `
    <tr>
      <td><strong>${escapeHtml(r.bookingReference)}</strong></td>
      <td>
        <div>${escapeHtml(r.guestName)}</div>
        <small style="color: var(--text-muted);">${escapeHtml(r.guestPhone)}</small>
      </td>
      <td>Room ${escapeHtml(r.roomNumber)}</td>
      <td>
        <button class="btn btn-danger btn-sm" onclick="performQuickCheckOut(${r.id}, '${escapeHtml(r.bookingReference)}')">
          Check Out
        </button>
      </td>
    </tr>
  `).join('');
}

async function performQuickCheckIn(reservationId, ref) {
  if (!confirm(`Confirm guest check-in for reservation ${ref}?`)) return;
  try {
    await api.post(`/api/reservations/${reservationId}/check-in`);
    showToast(`Reservation ${ref} successfully checked in!`, 'success');
    loadDashboardData();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function performQuickCheckOut(reservationId, ref) {
  if (!confirm(`Confirm guest check-out for reservation ${ref}? Ensure room key is returned.`)) return;
  try {
    await api.post(`/api/reservations/${reservationId}/check-out`);
    showToast(`Reservation ${ref} successfully checked out! Room marked available.`, 'success');
    loadDashboardData();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  loadDashboardData();
});

