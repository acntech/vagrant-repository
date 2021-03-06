import * as React from 'react';
import { Component, FunctionComponent, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import { Button, Container, Icon, Segment, Table } from 'semantic-ui-react';

import { Group, GroupState, RootState } from '../../models';
import { findGroups } from '../../state/actions';
import { LoadingIndicator, PrimaryFooter, PrimaryHeader } from '../../components';

interface ComponentStateProps {
    groupState: GroupState;
}

interface ComponentDispatchProps {
    findGroups: (name?: string) => Promise<any>;
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

    componentDidMount() {
        this.props.findGroups();
    }

    public render(): ReactNode {
        const { groupId, createGroup } = this.state;
        const { groups, loading } = this.props.groupState;

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

const GroupsFragment: FunctionComponent<GroupsFragmentProps> = (props: GroupsFragmentProps) => {
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
            <PrimaryFooter />
        </Container>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findGroups: (name?: string) => dispatch(findGroups(name))
});

const ConnectedHomeContainer = connect(mapStateToProps, mapDispatchToProps)(HomeContainer);

export { ConnectedHomeContainer as HomeContainer };