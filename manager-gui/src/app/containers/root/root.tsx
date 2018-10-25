import * as React from 'react';
import { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Route, Switch } from 'react-router';
import { BrowserRouter } from 'react-router-dom';
import { Container, Segment } from 'semantic-ui-react';

import { GroupState, RootState } from '../../models';
import { findGroups } from '../../state/actions';
import { HomeContainer, GroupContainer, NotFoundContainer } from '../';

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
                <Container>
                    <Segment vertical>
                        <Switch>
                            <Route path="/group/:groupId?" component={GroupContainer} />
                            <Route path="/" exact component={HomeContainer} />
                            <Route component={NotFoundContainer} />
                        </Switch>
                    </Segment>
                </Container>
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