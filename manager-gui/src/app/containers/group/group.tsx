import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Dimmer, Loader } from 'semantic-ui-react';

import { GroupState, RootState } from '../../models';
import { MainHeader } from '../../components';

interface ComponentStateProps {
    groupState: GroupState;
}

interface ComponentDispatchProps {
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps;

interface ComponentState {
    boxId?: number;
}

const initialState: ComponentState = {};

class GroupContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    public render(): ReactNode {
        const { boxId } = this.state;
        const { groupState } = this.props;
        const { loading } = groupState;

        if (boxId) {
            return <Redirect to={`/box/${boxId}`} />;
        } else if (loading) {
            return <Loading />;
        } else {
            return (
                <div>
                    <MainHeader title='Vagrant Repository Manager' />
                    <h3>Group</h3>
                </div>
            );
        }
    }
}

const Loading: SFC<{}> = () => {
    return (
        <div>
            <MainHeader title='Vagrant Repository Manager' />
            <Dimmer inverted active>
                <Loader>Loading</Loader>
            </Dimmer>
        </div>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({});

const ConnectedGroupContainer = connect(mapStateToProps, mapDispatchToProps)(GroupContainer);

export { ConnectedGroupContainer as GroupContainer };