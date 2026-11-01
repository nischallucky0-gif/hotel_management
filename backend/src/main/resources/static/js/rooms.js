/**
 * Rooms Page Controller
 */

let allRooms = [];

async function loadRooms() {
  const tbody = document.getElementById('rooms-table-tbody');
  tbody.innerHTML = `<tr><td colspan="7" class="empty-state"><div class="loading-spinner"></div> Loading rooms...</td></tr>`;

  try {
    const query = document.getElementById('room-search-input').value;
    const type = document.getElementById('room-type-filter').value;
    const status = document.getElementById('room-status-filter').value;

    const rooms = await api.get('/api/rooms', { query, type, status });
    allRooms = rooms;
    renderRoomsTable(rooms);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state" style="color: var(--danger);">Failed to load rooms: ${escapeHtml(err.message)}</td></tr>`;
    showToast(err.message, 'error');
  }
}

function renderRoomsTable(rooms) {
  const tbody = document.getElementById('rooms-table-tbody');
  document.getElementById('rooms-count').textContent = rooms.length;

  if (!rooms || rooms.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state">No rooms found matching criteria.</td></tr>`;
    return;
  }

  tbody.innerHTML = rooms.map(room => `
    <tr>
      <td><strong>Room ${escapeHtml(room.roomNumber)}</strong></td>
      <td><span class="badge" style="background:#e0e7ff; color:#3730a3;">${escapeHtml(room.type)}</span></td>
      <td><strong>${formatCurrency(room.pricePerNight)}</strong> <small style="color: var(--text-muted);">/ night</small></td>
      <td>👥 Up to ${room.maxOccupancy} guests</td>
      <td>${getStatusBadge(room.status)}</td>
      <td style="max-width: 250px; color: var(--text-muted); font-size: 13px;">${escapeHtml(room.description || '—')}</td>
      <td style="text-align: right; white-space: nowrap;">
        <button class="btn btn-secondary btn-sm" onclick="editRoom(${room.id})">Edit</button>
        <button class="btn btn-outline btn-sm" onclick="toggleRoomMaintenance(${room.id}, '${room.status}')">
          ${room.status === 'MAINTENANCE' ? 'Set Available' : 'Maintenance'}
        </button>
        <button class="btn btn-danger btn-sm" onclick="deleteRoom(${room.id}, '${escapeHtml(room.roomNumber)}')">Delete</button>
      </td>
    </tr>
  `).join('');
}

let searchDebounceTimeout;
function handleRoomFilter() {
  clearTimeout(searchDebounceTimeout);
  searchDebounceTimeout = setTimeout(loadRooms, 250);
}

function resetRoomFilters() {
  document.getElementById('room-search-input').value = '';
  document.getElementById('room-type-filter').value = '';
  document.getElementById('room-status-filter').value = '';
  loadRooms();
}

function openRoomModal(isEdit = false) {
  const modal = document.getElementById('room-modal');
  document.getElementById('room-modal-title').textContent = isEdit ? 'Edit Room' : 'Add New Room';
  modal.classList.add('show');
}

function closeRoomModal() {
  const modal = document.getElementById('room-modal');
  modal.classList.remove('show');
  document.getElementById('room-form').reset();
  document.getElementById('room-id').value = '';
}

function editRoom(id) {
  const room = allRooms.find(r => r.id === id);
  if (!room) return;

  document.getElementById('room-id').value = room.id;
  document.getElementById('room-number').value = room.roomNumber;
  document.getElementById('room-type').value = room.type;
  document.getElementById('room-price').value = room.pricePerNight;
  document.getElementById('room-occupancy').value = room.maxOccupancy;
  document.getElementById('room-status').value = room.status;
  document.getElementById('room-desc').value = room.description || '';

  openRoomModal(true);
}

async function saveRoom(event) {
  event.preventDefault();
  const id = document.getElementById('room-id').value;
  const isEdit = Boolean(id);

  const payload = {
    roomNumber: document.getElementById('room-number').value.trim(),
    type: document.getElementById('room-type').value,
    pricePerNight: parseFloat(document.getElementById('room-price').value),
    maxOccupancy: parseInt(document.getElementById('room-occupancy').value, 10),
    status: document.getElementById('room-status').value,
    description: document.getElementById('room-desc').value.trim()
  };

  const btn = document.getElementById('btn-save-room');
  btn.disabled = true;
  btn.textContent = 'Saving...';

  try {
    if (isEdit) {
      await api.put(`/api/rooms/${id}`, payload);
      showToast(`Room ${payload.roomNumber} updated successfully`, 'success');
    } else {
      await api.post('/api/rooms', payload);
      showToast(`Room ${payload.roomNumber} created successfully`, 'success');
    }
    closeRoomModal();
    loadRooms();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Save Room';
  }
}

async function toggleRoomMaintenance(id, currentStatus) {
  const newStatus = currentStatus === 'MAINTENANCE' ? 'AVAILABLE' : 'MAINTENANCE';
  try {
    await api.patch(`/api/rooms/${id}/status?status=${newStatus}`);
    showToast(`Room status updated to ${newStatus}`, 'success');
    loadRooms();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function deleteRoom(id, roomNumber) {
  if (!confirm(`Are you sure you want to delete Room ${roomNumber}? This cannot be undone.`)) {
    return;
  }

  try {
    await api.delete(`/api/rooms/${id}`);
    showToast(`Room ${roomNumber} deleted successfully`, 'success');
    loadRooms();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  loadRooms();
});

