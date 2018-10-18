import * as React from 'react';
import { render } from 'react-dom';

import 'semantic-ui-css/semantic.min.css';
import './index.css';

const Root = () => {
    return (
        <h1>Vagrant Repository Manager GUI</h1>
    );
};

render(
    <Root />,
    document.getElementById('root'),
);