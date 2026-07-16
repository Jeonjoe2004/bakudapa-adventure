import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import EmptyState from '../EmptyState'

describe('EmptyState', () => {
  it('renders message', () => {
    render(<EmptyState message="Nothing here" />)
    expect(screen.getByText('Nothing here')).toBeInTheDocument()
  })
})
