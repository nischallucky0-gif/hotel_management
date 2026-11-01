/**
 * Hotel Management System - Centralized API & UI Utilities
 */

const getInitialBaseUrl = () => {
  const custom = localStorage.getItem('CUSTOM_API_URL');
  if (custom) return custom.replace(/\/+$/, '');
  
  if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
    return window.location.port === '8088' ? '' : 'http://localhost:8088';
  }
  
  // Default to relative if served from backend, else empty
  return '';
};

let API_BASE_URL = getInitialBaseUrl();

// Demo Sample Dataset (Used as seamless offline/cloud fallback if backend is unreachable)
const DEMO_STORAGE_KEY = 'HMS_DEMO_DATA_V1';
function initDemoData() {
  const existing = localStorage.getItem(DEMO_STORAGE_KEY);
  if (existing) {
    try { return JSON.parse(existing); } catch (e) {}
  }

  const today = new Date().toISOString().split('T')[0];
  const initial = {
    rooms: [
      { id: 1, roomNumber: '101', type: 'SINGLE', pricePerNight: 75.00, maxOccupancy: 1, description: 'Standard Single Room with City View, High-Speed Wi-Fi', status: 'OCCUPIED' },
      { id: 2, roomNumber: '102', type: 'SINGLE', pricePerNight: 80.00, maxOccupancy: 1, description: 'Deluxe Single Room with Balcony and Workspace', status: 'AVAILABLE' },
      { id: 3, roomNumber: '201', type: 'DOUBLE', pricePerNight: 120.00, maxOccupancy: 2, description: 'Superior Double Queen Room with Garden View', status: 'AVAILABLE' },
      { id: 4, roomNumber: '202', type: 'DOUBLE', pricePerNight: 130.00, maxOccupancy: 2, description: 'Executive Double Room with Mini Bar and Smart TV', status: 'OCCUPIED' },
      { id: 5, roomNumber: '301', type: 'DELUXE', pricePerNight: 190.00, maxOccupancy: 3, description: 'Deluxe King Suite with Jacuzzi and Ocean Panorama', status: 'AVAILABLE' },
      { id: 6, roomNumber: '302', type: 'DELUXE', pricePerNight: 210.00, maxOccupancy: 3, description: 'Luxury Deluxe Suite with Private Terrace', status: 'MAINTENANCE' },
      { id: 7, roomNumber: '401', type: 'SUITE', pricePerNight: 350.00, maxOccupancy: 4, description: 'Presidential Suite with 2 Bedrooms, Living Lounge & Butler', status: 'AVAILABLE' },
      { id: 8, roomNumber: '402', type: 'SUITE', pricePerNight: 320.00, maxOccupancy: 4, description: 'Grand Penthouse Suite with Panoramic Skyline View', status: 'AVAILABLE' }
    ],
    guests: [
      { id: 1, fullName: 'Alexander Wright', phoneNumber: '+1-555-0192', email: 'alexander.wright@example.com', address: '742 Evergreen Terrace, Springfield', idReference: 'PASSPORT-US-98124' },
      { id: 2, fullName: 'Sophia Martinez', phoneNumber: '+1-555-0283', email: 'sophia.martinez@example.com', address: '1204 Sunset Blvd, Los Angeles, CA', idReference: 'DL-CA-449102' },
      { id: 3, fullName: 'David Chen', phoneNumber: '+1-555-0374', email: 'david.chen@example.com', address: '88 Orchard Road, Singapore', idReference: 'PASSPORT-SG-33109' },
      { id: 4, fullName: 'Emma Johnson', phoneNumber: '+1-555-0465', email: 'emma.johnson@example.com', address: '15 Victoria Street, London, UK', idReference: 'DL-UK-772911' },
      { id: 5, fullName: 'Marcus Vance', phoneNumber: '+1-555-0556', email: 'marcus.vance@example.com', address: '452 Pine Hill Road, Seattle, WA', idReference: 'PASSPORT-US-55102' }
    ],
    reservations: [
      { id: 1, bookingReference: 'RES-1001', guestId: 1, guestName: 'Alexander Wright', guestPhone: '+1-555-0192', guestEmail: 'alexander.wright@example.com', roomId: 1, roomNumber: '101', roomType: 'SINGLE', roomPricePerNight: 75.00, checkInDate: '2026-10-30', checkOutDate: '2026-11-03', numberOfNights: 4, numberOfGuests: 1, totalAmount: 300.00, status: 'CHECKED_IN', notes: 'Guest requested late checkout.', billId: 1, billStatus: 'PARTIALLY_PAID' },
      { id: 2, bookingReference: 'RES-1002', guestId: 2, guestName: 'Sophia Martinez', guestPhone: '+1-555-0283', guestEmail: 'sophia.martinez@example.com', roomId: 4, roomNumber: '202', roomType: 'DOUBLE', roomPricePerNight: 130.00, checkInDate: '2026-10-31', checkOutDate: '2026-11-04', numberOfNights: 4, numberOfGuests: 2, totalAmount: 520.00, status: 'CHECKED_IN', notes: 'VIP Guest anniversary stay.', billId: 2, billStatus: 'PAID' },
      { id: 3, bookingReference: 'RES-1003', guestId: 3, guestName: 'David Chen', guestPhone: '+1-555-0374', guestEmail: 'david.chen@example.com', roomId: 2, roomNumber: '102', roomType: 'SINGLE', roomPricePerNight: 80.00, checkInDate: today, checkOutDate: '2026-11-04', numberOfNights: 3, numberOfGuests: 1, totalAmount: 240.00, status: 'CONFIRMED', notes: 'Arriving around 4:00 PM via airport taxi.', billId: 3, billStatus: 'UNPAID' },
      { id: 4, bookingReference: 'RES-1004', guestId: 4, guestName: 'Emma Johnson', guestPhone: '+1-555-0465', guestEmail: 'emma.johnson@example.com', roomId: 5, roomNumber: '301', roomType: 'DELUXE', roomPricePerNight: 190.00, checkInDate: '2026-11-06', checkOutDate: '2026-11-10', numberOfNights: 4, numberOfGuests: 2, totalAmount: 760.00, status: 'CONFIRMED', notes: 'Request high floor quiet room.', billId: 4, billStatus: 'PARTIALLY_PAID' },
      { id: 5, bookingReference: 'RES-1005', guestId: 5, guestName: 'Marcus Vance', guestPhone: '+1-555-0556', guestEmail: 'marcus.vance@example.com', roomId: 7, roomNumber: '401', roomType: 'SUITE', roomPricePerNight: 350.00, checkInDate: '2026-10-26', checkOutDate: '2026-10-31', numberOfNights: 5, numberOfGuests: 3, totalAmount: 1750.00, status: 'CHECKED_OUT', notes: 'Executive corporate retreat.', billId: 5, billStatus: 'PAID' }
    ],
    bills: [
      { id: 1, billNumber: 'INV-1001', reservationId: 1, bookingReference: 'RES-1001', guestName: 'Alexander Wright', guestPhone: '+1-555-0192', guestEmail: 'alexander.wright@example.com', roomNumber: '101', roomType: 'SINGLE', roomPricePerNight: 75.00, checkInDate: '2026-10-30', checkOutDate: '2026-11-03', numberOfNights: 4, roomCharges: 300.00, additionalChargesTotal: 73.50, totalAmount: 373.50, paidAmount: 200.00, balanceAmount: 173.50, status: 'PARTIALLY_PAID', additionalCharges: [{ id: 1, serviceCategory: 'Food & Beverage', description: 'In-Room Dining: Steak & Wine', amount: 48.50, chargeDate: '2026-10-31T19:00:00' }, { id: 2, serviceCategory: 'Laundry', description: 'Express Suit Dry Cleaning', amount: 25.00, chargeDate: '2026-11-01T09:00:00' }], payments: [{ id: 1, transactionReference: 'TXN-A101-1', amount: 200.00, paymentMethod: 'CARD', paymentDate: '2026-10-30T14:30:00', notes: 'Simulated Credit Card Advance' }] },
      { id: 2, billNumber: 'INV-1002', reservationId: 2, bookingReference: 'RES-1002', guestName: 'Sophia Martinez', guestPhone: '+1-555-0283', guestEmail: 'sophia.martinez@example.com', roomNumber: '202', roomType: 'DOUBLE', roomPricePerNight: 130.00, checkInDate: '2026-10-31', checkOutDate: '2026-11-04', numberOfNights: 4, roomCharges: 520.00, additionalChargesTotal: 65.00, totalAmount: 585.00, paidAmount: 585.00, balanceAmount: 0.00, status: 'PAID', additionalCharges: [{ id: 3, serviceCategory: 'Room Service', description: 'Champagne & Fruit Platter', amount: 65.00, chargeDate: '2026-10-31T20:00:00' }], payments: [{ id: 2, transactionReference: 'TXN-B202-1', amount: 585.00, paymentMethod: 'UPI', paymentDate: '2026-10-31T20:30:00', notes: 'Simulated Full UPI Payment' }] },
      { id: 3, billNumber: 'INV-1003', reservationId: 3, bookingReference: 'RES-1003', guestName: 'David Chen', guestPhone: '+1-555-0374', guestEmail: 'david.chen@example.com', roomNumber: '102', roomType: 'SINGLE', roomPricePerNight: 80.00, checkInDate: today, checkOutDate: '2026-11-04', numberOfNights: 3, roomCharges: 240.00, additionalChargesTotal: 0.00, totalAmount: 240.00, paidAmount: 0.00, balanceAmount: 240.00, status: 'UNPAID', additionalCharges: [], payments: [] },
      { id: 4, billNumber: 'INV-1004', reservationId: 4, bookingReference: 'RES-1004', guestName: 'Emma Johnson', guestPhone: '+1-555-0465', guestEmail: 'emma.johnson@example.com', roomNumber: '301', roomType: 'DELUXE', roomPricePerNight: 190.00, checkInDate: '2026-11-06', checkOutDate: '2026-11-10', numberOfNights: 4, roomCharges: 760.00, additionalChargesTotal: 0.00, totalAmount: 760.00, paidAmount: 380.00, balanceAmount: 380.00, status: 'PARTIALLY_PAID', additionalCharges: [], payments: [{ id: 3, transactionReference: 'TXN-D301-1', amount: 380.00, paymentMethod: 'CARD', paymentDate: '2026-11-01T10:00:00', notes: 'Simulated 50% Deposit' }] },
      { id: 5, billNumber: 'INV-1005', reservationId: 5, bookingReference: 'RES-1005', guestName: 'Marcus Vance', guestPhone: '+1-555-0556', guestEmail: 'marcus.vance@example.com', roomNumber: '401', roomType: 'SUITE', roomPricePerNight: 350.00, checkInDate: '2026-10-26', checkOutDate: '2026-10-31', numberOfNights: 5, roomCharges: 1750.00, additionalChargesTotal: 95.00, totalAmount: 1845.00, paidAmount: 1845.00, balanceAmount: 0.00, status: 'PAID', additionalCharges: [{ id: 4, serviceCategory: 'Airport Shuttle', description: 'Luxury Mercedes Van Transfer', amount: 95.00, chargeDate: '2026-10-31T12:00:00' }], payments: [{ id: 4, transactionReference: 'TXN-E401-1', amount: 1845.00, paymentMethod: 'CASH', paymentDate: '2026-10-31T12:30:00', notes: 'Simulated Corporate Cash Settlement' }] }
    ]
  };

  saveDemoData(initial);
  return initial;
}

function saveDemoData(data) {
  try { localStorage.setItem(DEMO_STORAGE_KEY, JSON.stringify(data)); } catch (e) {}
}

const api = {
  async request(endpoint, options = {}) {
    const base = API_BASE_URL;
    const url = endpoint.startsWith('http') ? endpoint : `${base}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    };

    try {
      const response = await fetch(url, {
        ...options,
        headers
      });

      if (response.status === 204) return null;

      const contentType = response.headers.get('content-type');
      const isJson = contentType && contentType.includes('application/json');
      const data = isJson ? await response.json() : await response.text();

      if (!response.ok) {
        const errorMessage = (data && data.message) ? data.message : (data && data.error) ? data.error : `Request failed with status ${response.status}`;
        const details = (data && data.details && Array.isArray(data.details)) ? `\n• ${data.details.join('\n• ')}` : '';
        throw new Error(`${errorMessage}${details}`);
      }

      return data;
    } catch (err) {
      // If running on a cloud deployment (like Vercel) and backend server is offline/blocked by Mixed Content:
      if (!window.location.hostname.includes('localhost') && !window.location.hostname.includes('127.0.0.1')) {
        console.warn(`[HMS Cloud Fallback] Live backend unreachable at ${url}. Serving local fallback demo data.`, err);
        return handleDemoFallback(endpoint, options);
      }
      console.error(`API Error on [${options.method || 'GET'}] ${endpoint}:`, err);
      throw err;
    }
  },

  get(endpoint, params = {}) {
    const query = new URLSearchParams();
    Object.keys(params).forEach(key => {
      if (params[key] !== null && params[key] !== undefined && params[key] !== '') {
        query.append(key, params[key]);
      }
    });
    const queryString = query.toString() ? `?${query.toString()}` : '';
    return this.request(`${endpoint}${queryString}`, { method: 'GET' });
  },

  post(endpoint, body = {}) {
    return this.request(endpoint, { method: 'POST', body: JSON.stringify(body) });
  },

  put(endpoint, body = {}) {
    return this.request(endpoint, { method: 'PUT', body: JSON.stringify(body) });
  },

  patch(endpoint, body = {}) {
    return this.request(endpoint, { method: 'PATCH', body: JSON.stringify(body) });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }
};

/**
 * Cloud / Demo Fallback Handler
 */
function handleDemoFallback(endpoint, options) {
  const data = initDemoData();
  const method = (options.method || 'GET').toUpperCase();
  const path = endpoint.split('?')[0];

  if (path === '/api/dashboard/stats') {
    const totalRooms = data.rooms.length;
    const avail = data.rooms.filter(r => r.status === 'AVAILABLE').length;
    const occ = data.rooms.filter(r => r.status === 'OCCUPIED').length;
    const maint = data.rooms.filter(r => r.status === 'MAINTENANCE').length;
    const rev = data.bills.reduce((sum, b) => sum + (b.paidAmount || 0), 0);
    const today = new Date().toISOString().split('T')[0];
    const arrivals = data.reservations.filter(r => r.checkInDate === today && r.status === 'CONFIRMED');
    const departures = data.reservations.filter(r => r.checkOutDate === today && r.status === 'CHECKED_IN');

    return {
      totalRooms,
      availableRooms: avail,
      occupiedRooms: occ,
      maintenanceRooms: maint,
      todayCheckIns: arrivals.length,
      todayCheckOuts: departures.length,
      totalReservations: data.reservations.length,
      totalRevenue: rev,
      occupancyRate: totalRooms > 0 ? Math.round((occ / totalRooms) * 100) : 0,
      recentReservations: data.reservations.slice(-5).reverse(),
      expectedArrivals: arrivals,
      expectedDepartures: departures
    };
  }

  if (path === '/api/rooms') {
    if (method === 'GET') return data.rooms;
    if (method === 'POST') {
      const body = JSON.parse(options.body || '{}');
      const newRoom = { id: Date.now(), ...body };
      data.rooms.push(newRoom);
      saveDemoData(data);
      return newRoom;
    }
  }

  if (path.startsWith('/api/rooms/')) {
    const id = parseInt(path.split('/')[3], 10);
    if (method === 'PUT') {
      const body = JSON.parse(options.body || '{}');
      const idx = data.rooms.findIndex(r => r.id === id);
      if (idx !== -1) {
        data.rooms[idx] = { ...data.rooms[idx], ...body };
        saveDemoData(data);
        return data.rooms[idx];
      }
    }
    if (method === 'DELETE') {
      data.rooms = data.rooms.filter(r => r.id !== id);
      saveDemoData(data);
      return null;
    }
  }

  if (path === '/api/guests') {
    if (method === 'GET') return data.guests;
    if (method === 'POST') {
      const body = JSON.parse(options.body || '{}');
      const newGuest = { id: Date.now(), ...body };
      data.guests.push(newGuest);
      saveDemoData(data);
      return newGuest;
    }
  }

  if (path === '/api/reservations') {
    if (method === 'GET') return data.reservations;
    if (method === 'POST') {
      const body = JSON.parse(options.body || '{}');
      const room = data.rooms.find(r => r.id === body.roomId);
      let guest = data.guests.find(g => g.id === body.guestId);
      if (!guest && body.guestFullName) {
        guest = { id: Date.now(), fullName: body.guestFullName, email: body.guestEmail, phoneNumber: body.guestPhoneNumber };
        data.guests.push(guest);
      }
      const inDate = new Date(body.checkInDate);
      const outDate = new Date(body.checkOutDate);
      const nights = Math.max(1, Math.ceil((outDate - inDate) / (1000 * 60 * 60 * 24)));
      const rate = room ? room.pricePerNight : 100;
      const total = nights * rate;

      const newRes = {
        id: Date.now(),
        bookingReference: 'RES-' + Math.random().toString(36).substring(2, 8).toUpperCase(),
        guestId: guest ? guest.id : 1,
        guestName: guest ? guest.fullName : 'Guest',
        guestEmail: guest ? guest.email : '',
        guestPhone: guest ? guest.phoneNumber : '',
        roomId: room ? room.id : 1,
        roomNumber: room ? room.roomNumber : '101',
        roomType: room ? room.type : 'SINGLE',
        roomPricePerNight: rate,
        checkInDate: body.checkInDate,
        checkOutDate: body.checkOutDate,
        numberOfNights: nights,
        numberOfGuests: body.numberOfGuests || 1,
        totalAmount: total,
        status: 'CONFIRMED',
        notes: body.notes || '',
        billId: Date.now() + 1,
        billStatus: 'UNPAID'
      };

      data.reservations.unshift(newRes);
      data.bills.unshift({
        id: newRes.billId,
        billNumber: 'INV-' + Math.floor(1000 + Math.random() * 9000),
        reservationId: newRes.id,
        bookingReference: newRes.bookingReference,
        guestName: newRes.guestName,
        guestEmail: newRes.guestEmail,
        guestPhone: newRes.guestPhone,
        roomNumber: newRes.roomNumber,
        roomType: newRes.roomType,
        roomPricePerNight: newRes.roomPricePerNight,
        checkInDate: newRes.checkInDate,
        checkOutDate: newRes.checkOutDate,
        numberOfNights: newRes.numberOfNights,
        roomCharges: total,
        additionalChargesTotal: 0,
        totalAmount: total,
        paidAmount: 0,
        balanceAmount: total,
        status: 'UNPAID',
        additionalCharges: [],
        payments: []
      });

      saveDemoData(data);
      return newRes;
    }
  }

  if (path.includes('/check-in')) {
    const id = parseInt(path.split('/')[3], 10);
    const res = data.reservations.find(r => r.id === id);
    if (res) {
      res.status = 'CHECKED_IN';
      const room = data.rooms.find(r => r.id === res.roomId);
      if (room) room.status = 'OCCUPIED';
      saveDemoData(data);
      return res;
    }
  }

  if (path.includes('/check-out')) {
    const id = parseInt(path.split('/')[3], 10);
    const res = data.reservations.find(r => r.id === id);
    if (res) {
      res.status = 'CHECKED_OUT';
      const room = data.rooms.find(r => r.id === res.roomId);
      if (room) room.status = 'AVAILABLE';
      saveDemoData(data);
      return res;
    }
  }

  if (path.includes('/cancel')) {
    const id = parseInt(path.split('/')[3], 10);
    const res = data.reservations.find(r => r.id === id);
    if (res) {
      res.status = 'CANCELLED';
      saveDemoData(data);
      return res;
    }
  }

  if (path === '/api/bills') return data.bills;
  if (path.startsWith('/api/bills/')) {
    const parts = path.split('/');
    const id = parseInt(parts[3], 10);
    const bill = data.bills.find(b => b.id === id);
    if (parts[4] === 'charges') {
      const body = JSON.parse(options.body || '{}');
      bill.additionalCharges.push({ id: Date.now(), serviceCategory: body.serviceCategory, description: body.description, amount: body.amount, chargeDate: new Date().toISOString() });
      bill.additionalChargesTotal += body.amount;
      bill.totalAmount += body.amount;
      bill.balanceAmount += body.amount;
      saveDemoData(data);
      return bill;
    }
    if (parts[4] === 'payments') {
      const body = JSON.parse(options.body || '{}');
      bill.payments.push({ id: Date.now(), transactionReference: 'TXN-' + Math.random().toString(36).substring(2, 8).toUpperCase(), amount: body.amount, paymentMethod: body.paymentMethod, paymentDate: new Date().toISOString(), notes: body.notes || 'Simulated Payment' });
      bill.paidAmount += body.amount;
      bill.balanceAmount = Math.max(0, bill.totalAmount - bill.paidAmount);
      bill.status = bill.balanceAmount <= 0 ? 'PAID' : 'PARTIALLY_PAID';
      saveDemoData(data);
      return bill;
    }
    return bill || data.bills[0];
  }

  if (path === '/api/reports/revenue') {
    const totalRev = data.bills.reduce((s, b) => s + (b.paidAmount || 0), 0);
    const totalBilled = data.bills.reduce((s, b) => s + (b.totalAmount || 0), 0);
    return {
      totalRevenue: totalRev,
      totalBilled: totalBilled,
      totalOutstanding: totalBilled - totalRev,
      totalBillsCount: data.bills.length,
      revenueByPaymentMethod: { CASH: 1845.00, CARD: 580.00, UPI: 585.00 },
      recentPayments: [
        { id: 1, transactionReference: 'TXN-A101-1', amount: 200.00, paymentMethod: 'CARD', paymentDate: new Date().toISOString(), notes: 'Simulated Card Deposit' },
        { id: 2, transactionReference: 'TXN-B202-1', amount: 585.00, paymentMethod: 'UPI', paymentDate: new Date().toISOString(), notes: 'Simulated Full UPI Payment' }
      ]
    };
  }

  if (path === '/api/reports/occupancy') {
    const total = data.rooms.length;
    const occ = data.rooms.filter(r => r.status === 'OCCUPIED').length;
    return {
      totalRooms: total,
      occupiedRooms: occ,
      availableRooms: data.rooms.filter(r => r.status === 'AVAILABLE').length,
      maintenanceRooms: data.rooms.filter(r => r.status === 'MAINTENANCE').length,
      occupancyPercentage: total > 0 ? Math.round((occ / total) * 100) : 0,
      roomsByType: { SINGLE: 2, DOUBLE: 2, DELUXE: 2, SUITE: 2 },
      occupiedByType: { SINGLE: 1, DOUBLE: 1, DELUXE: 0, SUITE: 0 }
    };
  }

  if (path === '/api/reports/bookings') return data.reservations;

  return [];
}

/**
 * Toast Notification System
 */
function showToast(message, type = 'info') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  
  const icon = type === 'success' ? '✓' : type === 'error' ? '✕' : type === 'warning' ? '⚠' : 'ℹ';
  toast.innerHTML = `
    <span style="font-weight: 700; font-size: 16px;">${icon}</span>
    <span style="flex: 1; font-size: 14px;">${escapeHtml(message)}</span>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(40px)';
    toast.style.transition = 'all 0.3s ease-in';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

/**
 * Formatting Utilities
 */
function formatCurrency(amount) {
  if (amount === null || amount === undefined) return '$0.00';
  const val = typeof amount === 'number' ? amount : parseFloat(amount);
  return isNaN(val) ? '$0.00' : `$${val.toFixed(2)}`;
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  const d = new Date(dateStr + 'T00:00:00');
  return isNaN(d) ? dateStr : d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return '—';
  const d = new Date(dateTimeStr);
  return isNaN(d) ? dateTimeStr : d.toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function getStatusBadge(status) {
  if (!status) return '';
  const normalized = status.toLowerCase();
  const label = status.replace(/_/g, ' ');
  return `<span class="badge badge-${normalized}">${label}</span>`;
}

/**
 * Update real-time clock in topbar
 */
function startLiveClock() {
  const clockEl = document.getElementById('live-clock');
  if (!clockEl) return;
  const update = () => {
    const now = new Date();
    clockEl.textContent = now.toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric'
    }) + ' • ' + now.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };
  update();
  setInterval(update, 1000);
}

document.addEventListener('DOMContentLoaded', () => {
  startLiveClock();
});
