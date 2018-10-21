import * as React from 'react';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { Container, Segment } from 'semantic-ui-react';

import store from './state/store';
import { MainHeader } from './components/main-header';
import { ErrorHandler, RootContainer } from './containers';

import 'semantic-ui-css/semantic.min.css';
import './index.css';

const Root = () => {
    return (
        <Provider store={store}>
            <ErrorHandler>
                <Container>
                    <MainHeader title='Vagrant Repository Manager' />
                    <Segment vertical>
                        <BrowserRouter>
                            <RootContainer />
                        </BrowserRouter>
                    </Segment>
                </Container>
            </ErrorHandler>
        </Provider>
    );
};

render(
    <Root />,
    document.getElementById('root')
);