import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import {
    Button,
    ButtonProps,
    Container,
    Icon,
    ModalProps,
    Segment, Table
} from 'semantic-ui-react';
import { formatBytes } from '../../core/utils';
import {
    Box,
    BoxState,
    Group,
    GroupState,
    Provider,
    ProviderState,
    RootState,
    Version,
    VersionState
} from '../../models';
import {
    deleteVersion,
    findBoxVersions,
    findGroups,
    findGroupBoxes,
    findVersionProviders
} from '../../state/actions';
import {
    ConfirmModal,
    LoadingIndicator,
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
    versionState: VersionState;
    providerState: ProviderState;
}

interface ComponentDispatchProps {
    deleteVersion: (versionId: number) => Promise<any>;
    findGroups: (name?: string) => Promise<any>;
    findGroupBoxes: (groupId: number) => Promise<any>;
    findBoxVersions: (boxId: number) => Promise<any>;
    findVersionProviders: (versionId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface ComponentState {
    group?: Group;
    box?: Box;
    version?: Version;
    providerId?: number;
    createProvider: boolean;
    editVersion: boolean;
    openDeleteVersionConfirmModal: boolean;
    deleteVersionConfirmed: boolean;
}

const initialState: ComponentState = {
    createProvider: false,
    editVersion: false,
    openDeleteVersionConfirmModal: false,
    deleteVersionConfirmed: false
};

class VersionContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId, boxId, versionId } = this.props.match.params;
        this.props.findGroups();
        this.props.findGroupBoxes(Number(groupId));
        this.props.findBoxVersions(Number(boxId));
        this.props.findVersionProviders(Number(versionId));
    }

    componentDidUpdate() {
        const { groupId, boxId, versionId } = this.props.match.params;
        const { group, box, version } = this.state;

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

        if (!version) {
            const { versions } = this.props.versionState;
            const currentVersion = versions.find((version) => version.id == versionId);
            if (currentVersion) {
                this.setState({ version: currentVersion });
            }
        }
    }

    public render(): ReactNode {
        const { groupId, boxId, versionId } = this.props.match.params;
        const {
            createProvider,
            group,
            box,
            version,
            providerId,
            editVersion,
            openDeleteVersionConfirmModal,
            deleteVersionConfirmed
        } = this.state;
        const { providerState } = this.props;
        const { providers, loading } = providerState;

        if (providerId) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (createProvider) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/create`} />;
        } else if (editVersion) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/edit`} />;
        } else if (deleteVersionConfirmed) {
            return <Redirect to={`/group/${groupId}/box/${boxId}`} />
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
        } else if (!version) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No version found'
                content={`Could not find version for ID ${versionId}`} />;
        } else {
            const versionProviders = providers.filter(provider => provider.version.id == versionId);

            return <VersionFragment
                group={group}
                box={box}
                version={version}
                providers={versionProviders}
                onTableRowClick={this.onTableRowClick}
                onCreateProviderButtonClick={this.onCreateProviderButtonClick}
                onEditVersionButtonClick={this.onEditVersionButtonClick}
                openDeleteVersionConfirmModal={openDeleteVersionConfirmModal}
                onDeleteVersionButtonClick={this.onDeleteVersionButtonClick}
                onDeleteVersionModalClose={this.onDeleteVersionModalClose}
                onDeleteVersionModalCloseButtonClick={this.onDeleteVersionModalCloseButtonClick} />;
        }
    }

    private onTableRowClick = (providerId: number) => {
        this.setState({ providerId: providerId });
    };

    private onCreateProviderButtonClick = () => {
        this.setState({ createProvider: true });
    };

    private onDeleteVersionButtonClick = () => {
        this.setState({ openDeleteVersionConfirmModal: true });
    };

    private onEditVersionButtonClick = () => {
        this.setState({ editVersion: true });
    };

    private onDeleteVersionModalClose = () => {
        this.setState({ openDeleteVersionConfirmModal: false });
    };

    private onDeleteVersionModalCloseButtonClick = (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => {
        const { versionId } = this.props.match.params;
        const { positive } = data;
        if (positive) {
            this.props.deleteVersion(versionId);
            this.setState({ openDeleteVersionConfirmModal: false, deleteVersionConfirmed: true });
        } else {
            this.setState({ openDeleteVersionConfirmModal: false });
        }
    };
}

interface VersionFragmentProps {
    group: Group;
    box: Box;
    version: Version;
    providers: Provider[];
    onTableRowClick: (providerId: number) => void;
    onCreateProviderButtonClick: () => void;
    openDeleteVersionConfirmModal: boolean;
    onEditVersionButtonClick: () => void;
    onDeleteVersionButtonClick: () => void;
    onDeleteVersionModalClose: (event: React.MouseEvent<HTMLElement>, data: ModalProps) => void;
    onDeleteVersionModalCloseButtonClick: (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => void;
}

const VersionFragment: SFC<VersionFragmentProps> = (props) => {
    const {
        group,
        box,
        version,
        providers,
        onTableRowClick,
        onCreateProviderButtonClick,
        onEditVersionButtonClick,
        onDeleteVersionButtonClick,
        openDeleteVersionConfirmModal,
        onDeleteVersionModalClose,
        onDeleteVersionModalCloseButtonClick
    } = props;
    const { id: groupId, name: groupName } = group;
    const { id: boxId, name: boxName } = box;
    const { id: versionId, name: versionName, description } = version;

    return (
        <Container>
            <ConfirmModal
                title='Delete Version'
                subtitle='This action can not be reversed'
                open={openDeleteVersionConfirmModal}
                onClose={onDeleteVersionModalClose}
                onClick={onDeleteVersionModalCloseButtonClick} />
            <PrimaryHeader />
            <SecondaryHeader subtitle={description}>
                <Link to='/'><Icon name='home' /></Link>{'/ '}
                <Link to={`/group/${groupId}`}>{groupName}</Link>{' / '}
                <Link to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>{' / '}
                <Link to={`/group/${groupId}/box/${boxId}/version/${versionId}`}>{versionName}</Link>
            </SecondaryHeader>
            <ProvidersFragment
                providers={providers}
                onTableRowClick={onTableRowClick}
                onCreateProviderButtonClick={onCreateProviderButtonClick}
                onDeleteVersionButtonClick={onDeleteVersionButtonClick}
                onEditVersionButtonClick={onEditVersionButtonClick} />
        </Container>
    );
};

interface ProvidersFragmentProps {
    providers: Provider[];
    onTableRowClick: (providerId: number) => void;
    onCreateProviderButtonClick: () => void;
    onDeleteVersionButtonClick: () => void;
    onEditVersionButtonClick: () => void;
}

const ProvidersFragment: SFC<ProvidersFragmentProps> = (props) => {
    const {
        providers,
        onTableRowClick,
        onCreateProviderButtonClick,
        onEditVersionButtonClick,
        onDeleteVersionButtonClick
    } = props;

    return (
        <Segment basic>
            <Button.Group>
                <Button primary size='tiny' onClick={onCreateProviderButtonClick}>
                    <Icon name='file' />New Provider
                </Button>
            </Button.Group>
            <Button.Group>
                <Button primary size='small' onClick={onEditVersionButtonClick}>
                    <Icon name='pencil' />Edit Version
                </Button>
            </Button.Group>
            <Button.Group>
                <Button primary size='small' onClick={onDeleteVersionButtonClick}>
                    <Icon name='delete' />Delete Version
                </Button>
            </Button.Group>
            <Table celled selectable>
                <Table.Header>
                    <Table.Row>
                        <Table.HeaderCell width={4}>Provider</Table.HeaderCell>
                        <Table.HeaderCell width={4}>Size</Table.HeaderCell>
                        <Table.HeaderCell width={8}>Checksum</Table.HeaderCell>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {providers.map((provider, index) => {
                        const { id, providerType, size, checksumType, checksum } = provider;
                        const formattedBytes = formatBytes(size, 0);
                        const formattedChecksum = checksum ? `${checksumType}: ${checksum}` : undefined;
                        return (
                            <Table.Row key={index} className='clickable-table-row' onClick={() => onTableRowClick(id)}>
                                <Table.Cell>{providerType}</Table.Cell>
                                <Table.Cell>{formattedBytes}</Table.Cell>
                                <Table.Cell>{formattedChecksum}</Table.Cell>
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
    versionState: state.versionState,
    providerState: state.providerState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    deleteVersion: (versionId: number) => dispatch(deleteVersion(versionId)),
    findGroups: (name?: string) => dispatch(findGroups(name)),
    findGroupBoxes: (groupId: number) => dispatch(findGroupBoxes(groupId)),
    findBoxVersions: (boxId: number) => dispatch(findBoxVersions(boxId)),
    findVersionProviders: (versionId: number) => dispatch(findVersionProviders(versionId))
});

const ConnectedVersionContainer = connect(mapStateToProps, mapDispatchToProps)(VersionContainer);

export { ConnectedVersionContainer as VersionContainer };