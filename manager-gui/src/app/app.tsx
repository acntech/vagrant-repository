import { Component, ReactNode } from 'react';
import * as React from 'react';
import { Provider } from 'react-redux';
import { IntlProvider } from "react-intl";

import store, { initializeLocales } from './state/store';
import { ErrorHandlerProvider } from './providers';
import { RootContainer } from './containers';

import 'semantic-ui-css/semantic.min.css';
import './index.css';

class App extends Component {

    constructor(props) {
        super(props);
        initializeLocales();
    }

    public render(): ReactNode {
        return (
            <Provider store={store}>
                <IntlProvider locale='en'>
                    <ErrorHandlerProvider>
                        <RootContainer />
                    </ErrorHandlerProvider>
                </IntlProvider>
            </Provider>
        );
    }
}

export { App };