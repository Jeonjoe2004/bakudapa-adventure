import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import ErrorState from '../ErrorState'

describe('ErrorState', () => {
  it('renders error message', () => {
    render(<ErrorState message="Something broke" />)
    expect(screen.getByText('Something broke')).toBeInTheDocument()
  })
})
