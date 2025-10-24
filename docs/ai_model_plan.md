# AI Model Plan

We will phase AI capabilities to reduce risk and ship the MVP quickly.

## Phase 1 (MVP): Deterministic with AI assist
- Food mapping: local database lookup (normalized names, synonyms, n-gram fuzzy match)
- Optional lightweight NLP: map user-entered text (e.g., "2 eggs and toast") into items and quantities; fall back to manual entry
- Data sources: USDA FoodData Central (open), Open Food Facts (barcodes), region-specific datasets; store a curated subset on-device

## Phase 2: Barcode + OCR
- Barcode scanning: use ML Kit to read UPC/EAN; query local cache then online API
- OCR: read nutrition labels to extract macros if missing; apply simple rules/regex to parse per-100g or per-serving values

## Phase 3: Image-based recognition
- Model: MobileNet/EfficientNet-Lite fine-tuned on common foods to classify dish + estimate portion (with hand/object reference)
- Pipeline: on-device TFLite for top-N classes, then merge with portion estimate to compute macros; allow manual correction

## Data & Labeling
- Curate top 500–1000 foods by region; ensure per-100g nutrition fields
- Augment with synonyms, multilingual names
- For vision: collect or use existing datasets (Food-101, UECFOOD, VIREO) and add portion annotations

## Evaluation
- Text mapping: top-1 accuracy, top-3 recall, median edit distance; target ≥90% top-3 recall
- Vision model: macro MAE per 100g and per-serving; confidence calibration (ECE)
- Barcode/OCR: successful extraction rate ≥95%; manual fallback within 10s

## UX Principles
- Always show confidence and allow easy manual overrides
- Keep a transparent source trail ("Data from USDA FDC, per 100g")
- Learn from corrections to improve ranking locally (on-device personalization)

## Privacy & Compute
- Prefer on-device inference; if server is used, send minimal necessary data, anonymized
- Cache results; work offline with last-known mappings
