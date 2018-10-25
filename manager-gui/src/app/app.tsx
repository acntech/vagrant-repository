import { Component, ReactNode } from 'react';
import * as React from 'react';
import { Provider } from 'react-redux';

import store from './state/store';
import { ErrorHandlerProvider } from './providers';
import { RootContainer } from './containers';

import 'semantic-ui-css/semantic.min.css';
import './index.css';

class App extends Component {

    public render(): ReactNode {
        return (
            <Provider store={store}>
                <ErrorHandlerProvider>
                    <RootContainer />
                </ErrorHandlerProvider>
            </Provider>
        );
    }
}

export { App };