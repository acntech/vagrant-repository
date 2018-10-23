import * as React from 'react';
import { render } from 'react-dom';

import { App } from './app';

import 'semantic-ui-css/semantic.min.css';
import './index.css';

render(
    <App />,
    document.getElementById('root')
);