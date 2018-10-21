import * as React from 'react';
import { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Route, Switch } from 'react-router';

import { RootState } from '../../models/types';
import { HomeContainer, NotFoundContainer } from '../index';

interface ComponentStateProps {
}

interface ComponentDispatchProps {
}

class Root extends Component {

    public render(): ReactNode {
        return (
            <Switch>
                <Route path="/" exact component={HomeContainer} />
                <Route component={NotFoundContainer} />
            </Switch>
        );
    }
}

const mapStateToProps = (state: RootState): ComponentStateProps => ({
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
});

const RootContainer = connect(mapStateToProps, mapDispatchToProps)(Root);

export { RootContainer };