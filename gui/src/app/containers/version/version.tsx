import * as React from 'react';
import { Component, FunctionComponent, ReactNode } from 'react';
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
    Provider,
    ProviderState,
    RootState,
    Version,
    VersionState
} from '../../models';
import {
    deleteVersion,
    findBoxVersions,
    findVersionProviders
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
    versionState: VersionState;
    providerState: ProviderState;
}

interface ComponentDispatchProps {
    deleteVersion: (versionId: number) => Promise<any>;
    findBoxVersions: (boxId: number) => Promise<any>;
    findVersionProviders: (versionId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface ComponentState {
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
        const { boxId, versionId } = this.props.match.params;
        this.props.findBoxVersions(Number(boxId));
        this.props.findVersionProviders(Number(versionId));
    }

    componentDidUpdate() {
        const { versionId } = this.props.match.params;
        const { version } = this.state;

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
            version,
            providerId,
            editVersion,
            openDeleteVersionConfirmModal,
            deleteVersionConfirmed
        } = this.state;
        const { loading: versionLoading } = this.props.versionState;
        const { providers, loading: providerLoading } = this.props.providerState;

        if (providerId) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}`} />;
        } else if (versionLoading || providerLoading) {
            return <LoadingIndicator />;
        } else if (createProvider) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/create`} />;
        } else if (editVersion) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/edit`} />;
        } else if (deleteVersionConfirmed) {
            return <Redirect to={`/group/${groupId}/box/${boxId}`} />
        } else if (!version) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No version found'
                content={`Could not find version for ID ${versionId}`} />;
        } else {
            const versionProviders = providers.filter(provider => provider.version.id == versionId);

            return <VersionFragment
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

const VersionFragment: FunctionComponent<VersionFragmentProps> = (props: VersionFragmentProps) => {
    const {
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
    const { id: versionId, name: versionName, description: versionDescription, box } = version;
    const { id: boxId, name: boxName, group } = box;
    const { id: groupId, name: groupName } = group;

    return (
        <Container>
            <ConfirmModal
                title='Delete Version'
                subtitle='This action can not be reversed'
                open={openDeleteVersionConfirmModal}
                onClose={onDeleteVersionModalClose}
                onClick={onDeleteVersionModalCloseButtonClick} />
            <PrimaryHeader />
            <SecondaryHeader subtitle={versionDescription}>
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
            <PrimaryFooter />
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

const ProvidersFragment: FunctionComponent<ProvidersFragmentProps> = (props: ProvidersFragmentProps) => {
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
                <Button primary size='tiny' onClick={onEditVersionButtonClick}>
                    <Icon name='pencil' />Edit Version
                </Button>
            </Button.Group>
            <Button.Group>
                <Button primary size='tiny' onClick={onDeleteVersionButtonClick}>
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
    versionState: state.versionState,
    providerState: state.providerState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    deleteVersion: (versionId: number) => dispatch(deleteVersion(versionId)),
    findBoxVersions: (boxId: number) => dispatch(findBoxVersions(boxId)),
    findVersionProviders: (versionId: number) => dispatch(findVersionProviders(versionId))
});

const ConnectedVersionContainer = connect(mapStateToProps, mapDispatchToProps)(VersionContainer);

export { ConnectedVersionContainer as VersionContainer };