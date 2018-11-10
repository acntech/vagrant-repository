import * as React from 'react';
import { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Route, Switch } from 'react-router';
import { BrowserRouter } from 'react-router-dom';

import { RootState } from '../../models';
import {
    CreateBoxContainer,
    CreateGroupContainer,
    CreateProviderContainer,
    CreateVersionContainer,
    HomeContainer,
    BoxContainer,
    GroupContainer,
    PageNotFoundErrorContainer,
    ProviderContainer,
    VersionContainer,
    UpdateProviderContainer
} from '../';

interface ComponentStateProps {
}

interface ComponentDispatchProps {
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps;

class RootContainer extends Component<ComponentProps> {

    public render(): ReactNode {
        return (
            <BrowserRouter>
                <Switch>
                    <Route path="/group/:groupId?/box/:boxId?/version/:versionId?/provider/:providerId?/update" exact component={UpdateProviderContainer} />
                    <Route path="/group/:groupId?/box/:boxId?/version/:versionId?/provider/:providerId?" exact component={ProviderContainer} />
                    <Route path="/group/:groupId?/box/:boxId?/version/:versionId?/create" exact component={CreateProviderContainer} />
                    <Route path="/group/:groupId?/box/:boxId?/version/:versionId?" exact component={VersionContainer} />
                    <Route path="/group/:groupId?/box/:boxId?/create" exact component={CreateVersionContainer} />
                    <Route path="/group/:groupId?/box/:boxId?" exact component={BoxContainer} />
                    <Route path="/group/:groupId?/create" exact component={CreateBoxContainer} />
                    <Route path="/group/:groupId?" exact component={GroupContainer} />
                    <Route path="/create" exact component={CreateGroupContainer} />
                    <Route path="/" exact component={HomeContainer} />
                    <Route component={PageNotFoundErrorContainer} />
                </Switch>
            </BrowserRouter>
        );
    }
}

const mapStateToProps = (state: RootState): ComponentStateProps => ({
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
});

const ConnectedRootContainer = connect(mapStateToProps, mapDispatchToProps)(RootContainer);

export { ConnectedRootContainer as RootContainer };