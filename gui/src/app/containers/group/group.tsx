import * as React from 'react';
import { Component, FunctionComponent, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import {
    Button,
    ButtonProps,
    Container,
    Icon, ModalProps,
    Segment,
    Table
} from 'semantic-ui-react';
import {
    Box,
    BoxState,
    Group,
    GroupState,
    RootState
} from '../../models';
import {
    deleteGroup,
    getGroup,
    findGroupBoxes
} from '../../state/actions';
import {
    ConfirmModal,
    LoadingIndicator,
    PrimaryFooter,
    PrimaryHeader,
    SecondaryHeader
} from '../../components';
import { NotFoundErrorContainer } from '../';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    groupState: GroupState;
    boxState: BoxState;
}

interface ComponentDispatchProps {
    deleteGroup: (groupId: number) => Promise<any>;
    getGroup: (groupId: number) => Promise<any>;
    findGroupBoxes: (groupId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface ComponentState {
    group?: Group;
    boxId?: number;
    createBox: boolean;
    editGroup: boolean;
    deleteGroupConfirmed: boolean;
    openDeleteGroupConfirmModal: boolean;
}

const initialState: ComponentState = {
    createBox: false,
    editGroup: false,
    deleteGroupConfirmed: false,
    openDeleteGroupConfirmModal: false
};

class GroupContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId } = this.props.match.params;
        groupId && this.props.getGroup(groupId);
        groupId && this.props.findGroupBoxes(groupId);
    }

    componentDidUpdate() {
        const { groupId } = this.props.match.params;
        let { group } = this.state;

        if (!group) {
            const { groups } = this.props.groupState;
            group = groups.find((group) => group.id == groupId);
            if (group) {
                this.setState({ group: group });
            }
        }
    }

    public render(): ReactNode {
        const { groupId } = this.props.match.params;
        const { loading: groupLoading } = this.props.groupState;
        const { boxes, loading: boxLoading } = this.props.boxState;
        const {
            boxId,
            group,
            createBox,
            editGroup,
            deleteGroupConfirmed,
            openDeleteGroupConfirmModal
        } = this.state;

        if (boxId) {
            return <Redirect to={`/group/${groupId}/box/${boxId}`} />;
        } else if (groupLoading || boxLoading) {
            return <LoadingIndicator />;
        } else if (createBox) {
            return <Redirect to={`/group/${groupId}/create`} />;
        } else if (editGroup) {
            return <Redirect to={`/group/${groupId}/edit`} />;
        } else if (deleteGroupConfirmed) {
            return <Redirect to='/' />
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
                onCreateBoxButtonClick={this.onCreateBoxButtonClick}
                onEditGroupButtonClick={this.onEditGroupButtonClick}
                openDeleteGroupConfirmModal={openDeleteGroupConfirmModal}
                onDeleteGroupButtonClick={this.onDeleteGroupButtonClick}
                onDeleteGroupModalClose={this.onDeleteGroupModalClose}
                onDeleteGroupModalCloseButtonClick={this.onDeleteGroupModalCloseButtonClick} />;
        }
    }

    private onTableRowClick = (boxId: number) => {
        this.setState({ boxId: boxId });
    };

    private onCreateBoxButtonClick = () => {
        this.setState({ createBox: true });
    };

    private onEditGroupButtonClick = () => {
        this.setState({ editGroup: true });
    };

    private onDeleteGroupButtonClick = () => {
        this.setState({ openDeleteGroupConfirmModal: true });
    };

    private onDeleteGroupModalClose = () => {
        this.setState({ openDeleteGroupConfirmModal: false });
    };

    private onDeleteGroupModalCloseButtonClick = (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => {
        const { groupId } = this.props.match.params;
        const { positive } = data;
        if (positive) {
            this.props.deleteGroup(groupId);
            this.setState({ openDeleteGroupConfirmModal: false, deleteGroupConfirmed: true });
        } else {
            this.setState({ openDeleteGroupConfirmModal: false });
        }
    };
}

interface GroupFragmentProps {
    group: Group;
    boxes: Box[];
    onTableRowClick: (boxId: number) => void;
    onCreateBoxButtonClick: () => void;
    openDeleteGroupConfirmModal: boolean;
    onEditGroupButtonClick: () => void;
    onDeleteGroupButtonClick: () => void;
    onDeleteGroupModalClose: (event: React.MouseEvent<HTMLElement>, data: ModalProps) => void;
    onDeleteGroupModalCloseButtonClick: (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => void;
}

const GroupFragment: FunctionComponent<GroupFragmentProps> = (props: GroupFragmentProps) => {
    const {
        group,
        boxes,
        onTableRowClick,
        onCreateBoxButtonClick,
        onEditGroupButtonClick,
        openDeleteGroupConfirmModal,
        onDeleteGroupButtonClick,
        onDeleteGroupModalClose,
        onDeleteGroupModalCloseButtonClick
    } = props;
    const { id, name, description } = group;

    return (
        <Container>
            <ConfirmModal
                title='Delete Box'
                subtitle='This action can not be reversed'
                open={openDeleteGroupConfirmModal}
                onClose={onDeleteGroupModalClose}
                onClick={onDeleteGroupModalCloseButtonClick} />
            <PrimaryHeader />
            <SecondaryHeader subtitle={description}>
                <Link to='/'><Icon name='home' /></Link>{'/ '}
                <Link to={`/group/${id}`}>{name}</Link>
            </SecondaryHeader>
            <BoxesFragment
                boxes={boxes}
                onTableRowClick={onTableRowClick}
                onCreateBoxButtonClick={onCreateBoxButtonClick}
                onEditGroupButtonClick={onEditGroupButtonClick}
                onDeleteGroupButtonClick={onDeleteGroupButtonClick} />
            <PrimaryFooter />
        </Container>
    );
};

interface BoxesFragmentProps {
    boxes: Box[];
    onTableRowClick: (boxId: number) => void;
    onCreateBoxButtonClick: () => void;
    onEditGroupButtonClick: () => void;
    onDeleteGroupButtonClick: () => void;
}

const BoxesFragment: FunctionComponent<BoxesFragmentProps> = (props: BoxesFragmentProps) => {
    const {
        boxes,
        onTableRowClick,
        onCreateBoxButtonClick,
        onEditGroupButtonClick,
        onDeleteGroupButtonClick
    } = props;

    return (
        <Segment basic>
            <Button.Group>
                <Button primary size='tiny' onClick={onCreateBoxButtonClick}>
                    <Icon name='cube' />New Box
                </Button>
            </Button.Group>
            <Button.Group>
                <Button primary size='tiny' onClick={onEditGroupButtonClick}>
                    <Icon name='pencil' />Edit Group
                </Button>
            </Button.Group>
            <Button.Group>
                <Button primary size='tiny' onClick={onDeleteGroupButtonClick}>
                    <Icon name='delete' />Delete Group
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
    deleteGroup: (groupId: number) => dispatch(deleteGroup(groupId)),
    getGroup: (groupId: number) => dispatch(getGroup(groupId)),
    findGroupBoxes: (groupId: number) => dispatch(findGroupBoxes(groupId))
});

const ConnectedGroupContainer = connect(mapStateToProps, mapDispatchToProps)(GroupContainer);

export { ConnectedGroupContainer as GroupContainer };