import * as React from 'react';
import { Component, FunctionComponent, ReactNode } from 'react';
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
    RootState,
    Version,
    VersionState
} from '../../models';
import {
    deleteBox,
    findBoxVersions,
    findGroupBoxes
} from '../../state/actions';
import { ConfirmModal, LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';
import { NotFoundErrorContainer } from '../';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    boxState: BoxState;
    versionState: VersionState;
}

interface ComponentDispatchProps {
    deleteBox: (boxId: number) => Promise<any>;
    findGroupBoxes: (groupId: number) => Promise<any>;
    findBoxVersions: (boxId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface ComponentState {
    box?: Box;
    versionId?: number;
    createVersion: boolean;
    editBox: boolean;
    deleteBoxConfirmed: boolean;
    openDeleteBoxConfirmModal: boolean;
}

const initialState: ComponentState = {
    createVersion: false,
    editBox: false,
    deleteBoxConfirmed: false,
    openDeleteBoxConfirmModal: false
};

class BoxContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId, boxId } = this.props.match.params;
        this.props.findGroupBoxes(Number(groupId));
        this.props.findBoxVersions(Number(boxId));
    }

    componentDidUpdate() {
        const { boxId } = this.props.match.params;
        const { box } = this.state;

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
            box,
            createVersion,
            editBox,
            deleteBoxConfirmed,
            openDeleteBoxConfirmModal
        } = this.state;
        const { loading: boxLoading } = this.props.boxState;
        const { versions, loading: versionLoading } = this.props.versionState;

        if (versionId) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />;
        } else if (boxLoading || versionLoading) {
            return <LoadingIndicator />;
        } else if (createVersion) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/create`} />;
        } else if (editBox) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/edit`} />;
        } else if (deleteBoxConfirmed) {
            return <Redirect to={`/group/${groupId}`} />
        } else if (!box) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No box found'
                content={`Could not find box for ID ${boxId}`} />;
        } else {
            const boxVersions = versions.filter(version => version.box.id == boxId);

            return <BoxFragment
                box={box}
                versions={boxVersions}
                onTableRowClick={this.onTableRowClick}
                onCreateVersionButtonClick={this.onCreateVersionButtonClick}
                onEditBoxButtonClick={this.onEditBoxButtonClick}
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

    private onEditBoxButtonClick = () => {
        this.setState({ editBox: true });
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
    box: Box;
    versions: Version[];
    onTableRowClick: (versionId: number) => void;
    onCreateVersionButtonClick: () => void;
    onEditBoxButtonClick: () => void;
    openDeleteBoxConfirmModal: boolean;
    onDeleteBoxButtonClick: () => void;
    onDeleteBoxModalClose: (event: React.MouseEvent<HTMLElement>, data: ModalProps) => void;
    onDeleteBoxModalCloseButtonClick: (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => void;
}

const BoxFragment: FunctionComponent<BoxFragmentProps> = (props) => {
    const {
        box,
        versions,
        onTableRowClick,
        onCreateVersionButtonClick,
        onEditBoxButtonClick,
        openDeleteBoxConfirmModal,
        onDeleteBoxButtonClick,
        onDeleteBoxModalClose,
        onDeleteBoxModalCloseButtonClick
    } = props;
    const { id: boxId, name: boxName, description: boxDescription, group } = box;
    const { id: groupId, name: groupName } = group;

    return (
        <Container>
            <ConfirmModal
                title='Delete Box'
                subtitle='This action can not be reversed'
                open={openDeleteBoxConfirmModal}
                onClose={onDeleteBoxModalClose}
                onClick={onDeleteBoxModalCloseButtonClick} />
            <PrimaryHeader />
            <SecondaryHeader subtitle={boxDescription}>
                <Link to='/'><Icon name='home' /></Link>{'/ '}
                <Link to={`/group/${groupId}`}>{groupName}</Link>{' / '}
                <Link to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>
            </SecondaryHeader>
            <VersionsFragment
                versions={versions}
                onTableRowClick={onTableRowClick}
                onCreateVersionButtonClick={onCreateVersionButtonClick}
                onEditBoxButtonClick={onEditBoxButtonClick}
                onDeleteBoxButtonClick={onDeleteBoxButtonClick} />
        </Container>
    );
};

interface VersionsFragmentProps {
    versions: Version[];
    onTableRowClick: (versionId: number) => void;
    onCreateVersionButtonClick: () => void;
    onEditBoxButtonClick: () => void;
    onDeleteBoxButtonClick: () => void;
}

const VersionsFragment: FunctionComponent<VersionsFragmentProps> = (props) => {
    const {
        versions,
        onTableRowClick,
        onCreateVersionButtonClick,
        onEditBoxButtonClick,
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
                <Button primary size='tiny' onClick={onEditBoxButtonClick}>
                    <Icon name='pencil' />Edit Box
                </Button>
            </Button.Group>
            <Button.Group>
                <Button primary size='tiny' onClick={onDeleteBoxButtonClick}>
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
    boxState: state.boxState,
    versionState: state.versionState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    deleteBox: (boxId: number) => dispatch(deleteBox(boxId)),
    findGroupBoxes: (groupId: number) => dispatch(findGroupBoxes(groupId)),
    findBoxVersions: (boxId: number) => dispatch(findBoxVersions(boxId))
});

const ConnectedBoxContainer = connect(mapStateToProps, mapDispatchToProps)(BoxContainer);

export { ConnectedBoxContainer as BoxContainer };