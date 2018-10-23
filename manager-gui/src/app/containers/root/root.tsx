import * as React from 'react';
import { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Route, Switch } from 'react-router';
import { Container, Segment } from 'semantic-ui-react';
import { MainHeader } from '../../components/main-header';

import { GroupState, RootState } from '../../models';
import { findGroups } from '../../state/actions';
import { HomeContainer, NotFoundContainer } from '../';

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
            <Container>
                <MainHeader title='Vagrant Repository Manager' />
                <Segment vertical>
                    <Switch>
                        <Route path="/" exact component={HomeContainer} />
                        <Route component={NotFoundContainer} />
                    </Switch>
                </Segment>
            </Container>
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