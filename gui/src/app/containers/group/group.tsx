import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { Button, Container, Icon, Segment, Table } from 'semantic-ui-react';

import { Box, BoxState, Group, GroupState, RootState } from '../../models';
import { findGroups, findGroupBoxes } from '../../state/actions';
import { LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';
import { NotFoundErrorContainer } from '../';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    groupState: GroupState;
    boxState: BoxState;
}

interface ComponentDispatchProps {
    findGroups: (name?: string) => Promise<any>;
    findGroupBoxes: (groupId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface ComponentState {
    group?: Group;
    boxId?: number;
    createBox: boolean;
}

const initialState: ComponentState = {
    createBox: false
};

class GroupContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId } = this.props.match.params;
        this.props.findGroups();
        this.props.findGroupBoxes(Number(groupId));
    }

    componentDidUpdate() {
        const { groupId } = this.props.match.params;
        const { group } = this.state;

        if (!group) {
            const { groups } = this.props.groupState;
            const currentGroup = groups.find((group) => group.id == groupId);
            if (currentGroup) {
                this.setState({ group: currentGroup });
            }
        }
    }

    public render(): ReactNode {
        const { groupId } = this.props.match.params;
        const { boxes, loading } = this.props.boxState;
        const { boxId, group, createBox } = this.state;

        if (boxId) {
            return <Redirect to={`/group/${groupId}/box/${boxId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (createBox) {
            return <Redirect to={`/group/${groupId}/create`} />;
        } else if (!group) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No group found'
                content={`Could not find group for ID ${groupId}`} />;
        } else {
            const groupBoxes = boxes.filter(box => box.group.id == groupId);

            return <GroupFragment
                group={group}
                boxes={groupBoxes}
                onTableRowClick={this.onTableRowClick}
                onCreateBoxButtonClick={this.onCreateBoxButtonClick} />;
        }
    }

    private onTableRowClick = (boxId: number) => {
        this.setState({ boxId: boxId });
    };

    private onCreateBoxButtonClick = () => {
        this.setState({ createBox: true });
    };
}

interface GroupFragmentProps {
    group: Group;
    boxes: Box[];
    onTableRowClick: (boxId: number) => void;
    onCreateBoxButtonClick: () => void;
}

const GroupFragment: SFC<GroupFragmentProps> = (props) => {
    const { group, boxes, onTableRowClick, onCreateBoxButtonClick } = props;
    const { id, name, description } = group;

    return (
        <Container>
            <PrimaryHeader />
            <SecondaryHeader subtitle={description}>
                <Link to='/'><Icon name='home' /></Link>{'/ '}
                <Link to={`/group/${id}`}>{name}</Link>
            </SecondaryHeader>
            <BoxesFragment
                boxes={boxes}
                onTableRowClick={onTableRowClick}
                onCreateBoxButtonClick={onCreateBoxButtonClick} />
        </Container>
    );
};

interface BoxesFragmentProps {
    boxes: Box[];
    onTableRowClick: (boxId: number) => void;
    onCreateBoxButtonClick: () => void;
};

const BoxesFragment: SFC<BoxesFragmentProps> = (props) => {
    const { boxes, onTableRowClick, onCreateBoxButtonClick } = props;

    return (
        <Segment basic>
            <Button.Group>
                <Button primary size='tiny' onClick={onCreateBoxButtonClick}>
                    <Icon name='cube' />New Box
                </Button>
            </Button.Group>
            <Table celled selectable>
                <Table.Header>
                    <Table.Row>
                        <Table.HeaderCell width={4}>Box name</Table.HeaderCell>
                        <Table.HeaderCell width={10}>Description</Table.HeaderCell>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {boxes.map((box, index) => {
                        const {
                            id: boxId,
                            name: boxName,
                            description: boxDescription } = box;
                        return (
                            <Table.Row
                                key={index}
                                className='clickable-table-row'
                                onClick={() => onTableRowClick(boxId)}>
                                <Table.Cell>{boxName}</Table.Cell>
                                <Table.Cell>{boxDescription}</Table.Cell>
                            </Table.Row>
                        );
                    })}
                </Table.Body>
            </Table>
        </Segment>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState,
    boxState: state.boxState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findGroups: (name?: string) => dispatch(findGroups(name)),
    findGroupBoxes: (groupId: number) => dispatch(findGroupBoxes(groupId))
});

const ConnectedGroupContainer = connect(mapStateToProps, mapDispatchToProps)(GroupContainer);

export { ConnectedGroupContainer as GroupContainer };