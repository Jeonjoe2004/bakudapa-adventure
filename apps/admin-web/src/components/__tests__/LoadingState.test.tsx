import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import LoadingState from '../LoadingState'

describe('LoadingState', () => {
  it('renders spinner', () => {
    render(<LoadingState />)
    const el = document.querySelector('.animate-spin')
    expect(el).toBeTruthy()
  })
})
