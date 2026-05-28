import request from './request'

export function getList(params) {
  return request.get('/diary', { params })
}

export function getById(id) {
  return request.get(`/diary/${id}`)
}

export function create(data) {
  return request.post('/diary', data)
}

export function update(id, data) {
  return request.put(`/diary/${id}`, data)
}

export function remove(id) {
  return request.delete(`/diary/${id}`)
}

export function getArchive() {
  return request.get('/diary/archive')
}