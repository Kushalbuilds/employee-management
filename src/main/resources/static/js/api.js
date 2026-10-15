/**
 * Thin wrapper around fetch() for the Employee Management REST API.
 * Every function returns a Promise that resolves to parsed JSON, or
 * throws an ApiError with a user-friendly message + field errors.
 */

const API_BASE = '/api';

class ApiError extends Error {
  constructor(message, fieldErrors) {
    super(message);
    this.fieldErrors = fieldErrors || {};
  }
}

async function request(path, options = {}) {
  const res = await fetch(API_BASE + path, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });

  if (res.status === 204) return null;

  let body = null;
  try {
    body = await res.json();
  } catch (_) {
    // no body
  }

  if (!res.ok) {
    const message = (body && body.message) || `Request failed (${res.status})`;
    throw new ApiError(message, body && body.fieldErrors);
  }

  return body;
}

const EmployeeApi = {
  search(params = {}) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== null && v !== '') query.set(k, v);
    });
    return request(`/employees?${query.toString()}`);
  },
  get(id) {
    return request(`/employees/${id}`);
  },
  create(payload) {
    return request('/employees', { method: 'POST', body: JSON.stringify(payload) });
  },
  update(id, payload) {
    return request(`/employees/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
  },
  remove(id) {
    return request(`/employees/${id}`, { method: 'DELETE' });
  },
};

const DepartmentApi = {
  list() {
    return request('/departments');
  },
  create(payload) {
    return request('/departments', { method: 'POST', body: JSON.stringify(payload) });
  },
  update(id, payload) {
    return request(`/departments/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
  },
  remove(id) {
    return request(`/departments/${id}`, { method: 'DELETE' });
  },
};
