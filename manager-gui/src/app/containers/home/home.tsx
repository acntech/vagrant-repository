import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Dimmer, List, ListItemProps, Loader } from 'semantic-ui-react';

import { Group, GroupState, RootState } from '../../models';

interface ComponentStateProps {
    groupState: GroupState;
}

interface ComponentDispatchProps {
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps;

interface ComponentState {
    groupId?: string;
}

const initialState: ComponentState = {};

class HomeContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    public render(): ReactNode {
        const {groupId} = this.state;
        const {groupState} = this.props;
        const {groups, loading} = groupState;

        if (groupId) {
            return <Redirect to={`/group/${groupId}`} />;
        } else if (loading) {
            return <Loading />;
        } else {
            return <Groups groups={groups} onClick={this.onListItemClick} />;
        }
    }

    private onListItemClick = (event: React.MouseEvent<HTMLAnchorElement>, data: ListItemProps) => {
        const {value} = data;
        if (value) {
            this.setState({groupId: value});
        }
    };
}

interface GroupsProps {
    groups: Group[];
    onClick: (event: React.MouseEvent<HTMLAnchorElement>, data: ListItemProps) => void;
}

const Groups: SFC<GroupsProps> = (props) => {
    const {groups, onClick} = props;
    return (
        <List animated>
            {groups.map((group, index) => {
                const {id, name, description} = group;
                return (
                    <List.Item key={index} value={String(id)} onClick={onClick}>
                        <List.Content>
                            <List.Header>{name}</List.Header>
                            {description}
                        </List.Content>
                    </List.Item>
                );
            })}
        </List>
    );
};

const Loading: SFC<{}> = () => {
    return (
        <Dimmer inverted active>
            <Loader>Loading</Loader>
        </Dimmer>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({});

const ConnectedHomeContainer = connect(mapStateToProps, mapDispatchToProps)(HomeContainer);

export { ConnectedHomeContainer as HomeContainer };