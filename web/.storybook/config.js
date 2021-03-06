import { configure, addDecorator, addParameters } from '@storybook/react';
import { withA11y } from '@storybook/addon-a11y';
import theme from './theme';

import { withTests } from '@storybook/addon-jest';
import results from '../jest-test-results.json';

// Load all stories. They can be located anywhere inside /src. We want to keep
// our stories locally next to the component they belong to.
const req = require.context('../src', true, /\.stories\.(js|tsx)$/);
function loadStories() {
  req.keys().forEach(filename => req(filename));
}

addDecorator(
  withTests({
    results
  })
);

addParameters({
  options: {
    theme
  },
  backgrounds: [{ name: 'light', value: '#f0f4f8', default: true }]
});

addDecorator(withA11y);

configure(loadStories, module);
