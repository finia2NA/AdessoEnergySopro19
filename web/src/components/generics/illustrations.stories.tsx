import React from 'react';
import { storiesOf } from '@storybook/react';
import Illustrations from './illustrations';

storiesOf('Illustrations | Authentication', module)
  .addParameters({ jest: ['auth'] })
  .add('Auth', () => <Illustrations type="Auth" />)
  .add('Paper', () => <Illustrations type="NoSelected" />);
