import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Dimmer, Loader, Table } from 'semantic-ui-react';

import { Group, GroupState, RootState } from '../../models';
import { MainHeader } from '../../components';

interface ComponentStateProps {
    groupState: GroupState;
}

interface ComponentDispatchProps {
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps;

interface ComponentState {
    groupId?: number;
}

const initialState: ComponentState = {};

class HomeContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    public render(): ReactNode {
        const { groupId } = this.state;
        const { groupState } = this.props;
        const { groups, loading } = groupState;

        if (groupId) {
            return <Redirect to={`/group/${groupId}`} />;
        } else if (loading) {
            return <Loading />;
        } else {
            return (
                <Groups groups={groups} onClick={this.onListItemClick} />
            );
        }
    }

    private onListItemClick = (groupId: number) => {
        this.setState({ groupId: groupId });
    };
}

interface GroupsProps {
    groups: Group[];
    onClick: (groupId: number) => void;
}

const Groups: SFC<GroupsProps> = (props) => {
    const { groups, onClick } = props;
    return (
        <div>
            <MainHeader title='Vagrant Repository Manager' />
            <Table celled selectable>
                <Table.Header>
                    <Table.Row>
                        <Table.HeaderCell>Group</Table.HeaderCell>
                        <Table.HeaderCell>Description</Table.HeaderCell>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {groups.map((group, index) => {
                        const { id, name, description } = group;
                        return (
                            <Table.Row key={index} className='clickable-table-row' onClick={() => onClick(id)}>
                                <Table.Cell>{name}</Table.Cell>
                                <Table.Cell>{description}</Table.Cell>
                            </Table.Row>
                        );
                    })}
                </Table.Body>
            </Table>
        </div>
    );
};

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

const ConnectedHomeContainer = connect(mapStateToProps, mapDispatchToProps)(HomeContainer);

export { ConnectedHomeContainer as HomeContainer };