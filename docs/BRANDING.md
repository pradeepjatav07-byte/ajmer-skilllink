# Branding and logo assets

The original Nexoraa artwork supplied for this project is preserved byte-for-byte. The active frontend uses a transparent-background PNG derived from that artwork: only the surrounding dark matte and unused border are removed; the Nexoraa mark, wordmark, slogan, proportions, and original colors are retained.

## Files

- `public-assets/branding/nexoraa-logo-original.png` — untouched full supplied artwork.
- `public-assets/branding/nexoraa-logo-transparent.png` — full lockup with transparent exterior background.
- `public-assets/branding/nexoraa-symbol-crop.png` — untouched supplied symbol crop.
- `public-assets/branding/nexoraa-symbol-transparent.png` — compact transparent derivative.
- Matching runtime copies are under `frontend/public/branding/`.

`BrandImage` uses the transparent full logo on dark surfaces (login hero, loading screen, and portal sidebar). The redundant mini-logo on the white login card is omitted because the supplied wordmark is white and would lose contrast on white. The skill-passport stamp uses the transparent symbol. Keep the original files unchanged; do not recolor or redraw the supplied artwork.
