import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import { Button, Container, Icon, Segment, Table } from 'semantic-ui-react';

import { Group, GroupState, RootState } from '../../models';
import { LoadingIndicator, PrimaryHeader } from '../../components';

interface ComponentStateProps {
    groupState: GroupState;
}

interface ComponentDispatchProps {
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps;

interface ComponentState {
    groupId?: number;
    createGroup: boolean;
}

const initialState: ComponentState = {
    createGroup: false
};

class HomeContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    public render(): ReactNode {
        const { groupId, createGroup } = this.state;
        const { groupState } = this.props;
        const { groups, loading } = groupState;

        console.log(this.props.intl);

        if (groupId) {
            return <Redirect to={`/group/${groupId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (createGroup) {
            return <Redirect to='/create' />;
        } else {
            return (
                <GroupsFragment
                    groups={groups}
                    onTableRowClick={this.onTableRowClick}
                    onCreateGroupButtonClick={this.onCreateGroupButtonClick} />
            );
        }
    }

    private onTableRowClick = (groupId: number) => {
        this.setState({ groupId: groupId });
    };

    private onCreateGroupButtonClick = () => {
        this.setState({ createGroup: true });
    };
}

interface GroupsFragmentProps {
    groups: Group[];
    onTableRowClick: (groupId: number) => void;
    onCreateGroupButtonClick: () => void;
}

const GroupsFragment: SFC<GroupsFragmentProps> = (props) => {
    const { groups, onTableRowClick, onCreateGroupButtonClick } = props;

    return (
        <Container>
            <PrimaryHeader />
            <Segment basic>
                <Button.Group>
                    <Button primary size='tiny' onClick={onCreateGroupButtonClick}>
                        <Icon name='group' />New Group
                    </Button>
                </Button.Group>
                <Table celled selectable>
                    <Table.Header>
                        <Table.Row>
                            <Table.HeaderCell width={4}>Group</Table.HeaderCell>
                            <Table.HeaderCell width={10}>Description</Table.HeaderCell>
                        </Table.Row>
                    </Table.Header>
                    <Table.Body>
                        {groups.map((group, index) => {
                            const { id, name, description } = group;
                            return (
                                <Table.Row key={index} className='clickable-table-row' onClick={() => onTableRowClick(id)}>
                                    <Table.Cell>{name}</Table.Cell>
                                    <Table.Cell>{description}</Table.Cell>
                                </Table.Row>
                            );
                        })}
                    </Table.Body>
                </Table>
            </Segment>
        </Container>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
});

const ConnectedHomeContainer = connect(mapStateToProps, mapDispatchToProps)(HomeContainer);

export { ConnectedHomeContainer as HomeContainer };