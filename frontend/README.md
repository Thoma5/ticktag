## Compiling & executing
Make sure the backend is running on localhost:8080.
Compile and execute the frontend via `npm start` (may take a while) - it is available on localhost:3000.

## Lint
Check that your code conforms to the style settings by running `npm run-script lint`.
Ignore the linter warnings when running `npm start`, they are useless.

When using the IntelliJ autoformatting feature, make sure that you change "Other -> Generated Code -> Quote Marks" to
"Single Quotes" and check "Spaces -> Other -> After Type Reference Colon".

## Unit tests
Create a unit.spec.ts file next to the unit.ts file (see service/auth/ for a pretty useless example).
Run the tests via `npm test`.

## End-to-end tests
See app.component.e2e-spec.js for an example.
Make sure the app is running and then run the tests via `npm run-script e2e`
