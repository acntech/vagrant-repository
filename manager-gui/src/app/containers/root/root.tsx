import * as React from 'react';
import { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Route, Switch } from 'react-router';
import { BrowserRouter } from 'react-router-dom';

import { GroupState, RootState } from '../../models';
import { findGroups } from '../../state/actions';
import {
    CreateBoxContainer,
    CreateGroupContainer,
    CreateVersionContainer,
    HomeContainer,
    BoxContainer,
    GroupContainer,
    NotFoundContainer,
    VersionContainer
} from '../';

interface ComponentStateProps {
    groupState: GroupState;
}

interface ComponentDispatchProps {
    findGroups: (name?: string) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps;

class RootContainer extends Component<ComponentProps> {

    componentDidMount() {
        this.props.findGroups();
    }

    public render(): ReactNode {
        return (
            <BrowserRouter>
                <Switch>
                    <Route path="/group/:groupId?/box/:boxId?/version/:versionId?" component={VersionContainer} />
                    <Route path="/group/:groupId?/box/:boxId?/create/version" component={CreateVersionContainer} />
                    <Route path="/group/:groupId?/box/:boxId?" component={BoxContainer} />
                    <Route path="/group/:groupId?/create/box" component={CreateBoxContainer} />
                    <Route path="/group/:groupId?" component={GroupContainer} />
                    <Route path="/create/group" component={CreateGroupContainer} />
                    <Route path="/" exact component={HomeContainer} />
                    <Route component={NotFoundContainer} />
                </Switch>
            </BrowserRouter>
        );
    }
}

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findGroups: (name?: string) => dispatch(findGroups(name))
});

const ConnectedRootContainer = connect(mapStateToProps, mapDispatchToProps)(RootContainer);

export { ConnectedRootContainer as RootContainer };