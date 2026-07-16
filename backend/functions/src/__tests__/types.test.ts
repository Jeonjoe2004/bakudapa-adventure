import { describe, it, expect } from 'vitest'
import type { Mountain, Trail, AppUser, DashboardStats } from '../types'

describe('types', () => {
  it('Mountain shape is valid', () => {
    const m: Mountain = {
      name: 'Semeru',
      location: 'Jawa Timur',
      elevation: 3676,
      latitude: -8.1078,
      longitude: 112.9225,
      imageUrl: 'https://example.com/semeru.jpg',
      rating: 4.8,
      description: 'Gunung tertinggi di Jawa',
      difficulty: 'HARD',
      bestSeason: 'Apr-Oct',
      distance: 12.5,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    }
    expect(m.name).toBe('Semeru')
    expect(m.elevation).toBeGreaterThan(0)
  })

  it('Trail shape is valid', () => {
    const t: Trail = {
      name: 'Ranupane',
      mountainId: 'semeru_1',
      mountainName: 'Semeru',
      difficulty: 'MODERATE',
      distanceKm: 12.0,
      durationMinutes: 480,
      imageUrl: 'https://example.com/trail.jpg',
      elevationGain: 1500,
      maxElevation: 3676,
      popularity: 85,
      createdAt: Date.now(),
    }
    expect(t.name).toBeTruthy()
    expect(t.distanceKm).toBeGreaterThan(0)
  })

  it('AppUser defaults to user role', () => {
    const u: AppUser = {
      email: 'test@bakudapa.com',
      displayName: 'Test User',
      role: 'user',
      createdAt: Date.now(),
    }
    expect(u.role).toBe('user')
  })

  it('DashboardStats has activeToday placeholder', () => {
    const s: DashboardStats = {
      totalMountains: 5,
      totalUsers: 100,
      totalTrails: 20,
      totalPosts: 45,
      activeToday: 0,
      updatedAt: Date.now(),
    }
    expect(s.activeToday).toBe(0)
    expect(s.totalMountains + s.totalTrails).toBeGreaterThan(0)
  })
})
