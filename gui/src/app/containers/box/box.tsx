import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import {
    Button, ButtonProps,
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
    RootState,
    Version,
    VersionState
} from '../../models';
import {
    deleteBox,
    findBoxVersions,
    findGroups,
    findGroupBoxes
} from '../../state/actions';
import {ConfirmModal, LoadingIndicator, PrimaryHeader, SecondaryHeader} from '../../components';
import { NotFoundErrorContainer } from '../';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    groupState: GroupState;
    boxState: BoxState;
    versionState: VersionState;
}

interface ComponentDispatchProps {
    deleteBox: (boxId: number) => Promise<any>;
    findGroups: (name?: string) => Promise<any>;
    findGroupBoxes: (groupId: number) => Promise<any>;
    findBoxVersions: (boxId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface ComponentState {
    group?: Group;
    box?: Box;
    versionId?: number;
    createVersion: boolean;
    openDeleteBoxConfirmModal: boolean;
    deleteBoxConfirmed: boolean;
}

const initialState: ComponentState = {
    createVersion: false,
    openDeleteBoxConfirmModal: false,
    deleteBoxConfirmed: false
};

class BoxContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId, boxId } = this.props.match.params;
        this.props.findGroups();
        this.props.findGroupBoxes(Number(groupId));
        this.props.findBoxVersions(Number(boxId));
    }

    componentDidUpdate() {
        const { groupId, boxId } = this.props.match.params;
        const { group, box } = this.state;

        if (!group) {
            const { groups } = this.props.groupState;
            const currentGroup = groups.find((group) => group.id == groupId);
            if (currentGroup) {
                this.setState({ group: currentGroup });
            }
        }

        if (!box) {
            const { boxes } = this.props.boxState;
            const currentBox = boxes.find((box) => box.id == boxId);
            if (currentBox) {
                this.setState({ box: currentBox });
            }
        }
    }

    public render(): ReactNode {
        const { groupId, boxId } = this.props.match.params;
        const {
            versionId,
            group,
            box,
            createVersion,
            deleteBoxConfirmed,
            openDeleteBoxConfirmModal
        } = this.state;
        const { versionState } = this.props;
        const { versions, loading } = versionState;

        if (versionId) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (createVersion) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/create`} />;
        } else if (deleteBoxConfirmed) {
            return <Redirect to={`/group/${groupId}`} />
        } else if (!group) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No group found'
                content={`Could not find group for ID ${groupId}`} />;
        } else if (!box) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No box found'
                content={`Could not find box for ID ${boxId}`} />;
        } else {
            const boxVersions = versions.filter(version => version.box.id == boxId);

            return <BoxFragment
                group={group}
                box={box}
                versions={boxVersions}
                onTableRowClick={this.onTableRowClick}
                onCreateVersionButtonClick={this.onCreateVersionButtonClick}
                openDeleteBoxConfirmModal={openDeleteBoxConfirmModal}
                onDeleteBoxButtonClick={this.onDeleteBoxButtonClick}
                onDeleteBoxModalClose={this.onDeleteBoxModalClose}
                onDeleteBoxModalCloseButtonClick={this.onDeleteBoxModalCloseButtonClick} />;
        }
    }

    private onTableRowClick = (versionId: number) => {
        this.setState({ versionId: versionId });
    };

    private onCreateVersionButtonClick = () => {
        this.setState({ createVersion: true });
    };

    private onDeleteBoxButtonClick = () => {
        this.setState({ openDeleteBoxConfirmModal: true });
    };

    private onDeleteBoxModalClose = () => {
        this.setState({ openDeleteBoxConfirmModal: false });
    };

    private onDeleteBoxModalCloseButtonClick = (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => {
        const { boxId } = this.props.match.params;
        const { positive } = data;
        if (positive) {
            this.props.deleteBox(boxId);
            this.setState({ openDeleteBoxConfirmModal: false, deleteBoxConfirmed: true });
        } else {
            this.setState({ openDeleteBoxConfirmModal: false });
        }
    };
}

interface BoxFragmentProps {
    group: Group;
    box: Box;
    versions: Version[];
    onTableRowClick: (versionId: number) => void;
    onCreateVersionButtonClick: () => void;
    openDeleteBoxConfirmModal: boolean;
    onDeleteBoxButtonClick: () => void;
    onDeleteBoxModalClose: (event: React.MouseEvent<HTMLElement>, data: ModalProps) => void;
    onDeleteBoxModalCloseButtonClick: (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => void;
}

const BoxFragment: SFC<BoxFragmentProps> = (props) => {
    const {
        group,
        box,
        versions,
        onTableRowClick,
        onCreateVersionButtonClick,
        openDeleteBoxConfirmModal,
        onDeleteBoxButtonClick,
        onDeleteBoxModalClose,
        onDeleteBoxModalCloseButtonClick
    } = props;
    const { id: groupId, name: groupName } = group;
    const { id: boxId, name: boxName, description } = box;

    return (
        <Container>
            <ConfirmModal
                title='Delete Box'
                subtitle='This action can not be reversed'
                open={openDeleteBoxConfirmModal}
                onClose={onDeleteBoxModalClose}
                onClick={onDeleteBoxModalCloseButtonClick} />
            <PrimaryHeader />
            <SecondaryHeader subtitle={description}>
                <Link to='/'><Icon name='home' /></Link>{'/ '}
                <Link to={`/group/${groupId}`}>{groupName}</Link>{' / '}
                <Link to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>
            </SecondaryHeader>
            <VersionsFragment
                versions={versions}
                onTableRowClick={onTableRowClick}
                onCreateVersionButtonClick={onCreateVersionButtonClick}
                onDeleteBoxButtonClick={onDeleteBoxButtonClick} />
        </Container>
    );
};

interface VersionsFragmentProps {
    versions: Version[];
    onTableRowClick: (versionId: number) => void;
    onCreateVersionButtonClick: () => void;
    onDeleteBoxButtonClick: () => void;
}

const VersionsFragment: SFC<VersionsFragmentProps> = (props) => {
    const {
        versions,
        onTableRowClick,
        onCreateVersionButtonClick,
        onDeleteBoxButtonClick
    } = props;

    return (
        <Segment basic>
            <Button.Group>
                <Button primary size='tiny' onClick={onCreateVersionButtonClick}>
                    <Icon name='tag' />New Version
                </Button>
            </Button.Group>
            <Button.Group>
                <Button primary size='small' onClick={onDeleteBoxButtonClick}>
                    <Icon name='delete' />Delete Box
                </Button>
            </Button.Group>
            <Table celled selectable>
                <Table.Header>
                    <Table.Row>
                        <Table.HeaderCell width={4}>Version</Table.HeaderCell>
                        <Table.HeaderCell width={10}>Description</Table.HeaderCell>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {versions.map((version, index) => {
                        const { id, name, description } = version;
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
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState,
    boxState: state.boxState,
    versionState: state.versionState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    deleteBox: (boxId: number) => dispatch(deleteBox(boxId)),
    findGroups: (name?: string) => dispatch(findGroups(name)),
    findGroupBoxes: (groupId: number) => dispatch(findGroupBoxes(groupId)),
    findBoxVersions: (boxId: number) => dispatch(findBoxVersions(boxId))
});

const ConnectedBoxContainer = connect(mapStateToProps, mapDispatchToProps)(BoxContainer);

export { ConnectedBoxContainer as BoxContainer };