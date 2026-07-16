# ADR-001: Choose MapLibre for Android Maps

**Status:** Accepted

## Context
Android app needs offline map support for hiking in remote areas without signal.

## Options
1. **Google Maps** — rich features, easy setup, but no free offline tiles
2. **MapLibre** — open-source, supports offline tiles, no API key required for self-hosted style
3. **MapKit** — iOS only

## Decision
Use **MapLibre** for Android. Key factors:
- Offline tile download via `OfflineManager` for areas with no signal
- No usage quotas or billing (unlike Google Maps)
- Self-hosted or free tile server options
- Active open-source community

## Consequences
- Positive: free offline maps, no API key cost
- Positive: full control over tile styling
- Negative: fewer built-in features than Google Maps (street view, place autocomplete)
- Mitigation: use MapLibre only for map display; Google Places API for search if needed later

## Related
- iOS uses MapKit (Apple native, no offline without extra work)
