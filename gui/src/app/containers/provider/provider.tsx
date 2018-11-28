import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import {
    Button,
    ButtonProps,
    Container,
    Icon,
    ModalProps,
    Segment,
    Table
} from 'semantic-ui-react';

import { formatBytes } from '../../core/utils';
import {
    Box,
    BoxState,
    Group,
    GroupState,
    Provider,
    ProviderState,
    ProviderType,
    Version,
    VersionState,
    RootState
} from '../../models';
import { deleteProvider, getBox, getGroup, getProvider, getVersion } from '../../state/actions';
import { ConfirmModal, LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';
import { Link } from 'react-router-dom';
import { NotFoundErrorContainer } from '../error';

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
    deleteProvider: (providerId: number) => Promise<any>;
    getGroup: (groupId: number) => Promise<any>;
    getBox: (boxId: number) => Promise<any>;
    getVersion: (versionId: number) => Promise<any>;
    getProvider: (providerId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps & RouteProps;

interface ComponentState {
    group?: Group;
    box?: Box;
    version?: Version;
    provider?: Provider;
    openDeleteProviderConfirmModal: boolean;
    deleteProviderConfirmed: boolean;
}

const initialState: ComponentState = {
    openDeleteProviderConfirmModal: false,
    deleteProviderConfirmed: false
};

class ProviderContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId, boxId, versionId, providerId } = this.props.match.params;
        groupId && this.props.getGroup(groupId);
        boxId && this.props.getBox(boxId);
        versionId && this.props.getVersion(versionId);
        providerId && this.props.getProvider(providerId);
    }

    componentDidUpdate() {
        const { groupId, boxId, versionId, providerId } = this.props.match.params;
        let { group, box, version, provider } = this.state;

        if (!group) {
            const { groups } = this.props.groupState;
            group = groups.find(g => g.id == groupId);
            if (group) {
                this.setState({ group: group });
            }
        }

        if (!box) {
            const { boxes } = this.props.boxState;
            box = boxes.find(b => b.id == boxId);
            if (box) {
                this.setState({ box: box });
            }
        }

        if (!version) {
            const { versions } = this.props.versionState;
            version = versions.find(v => v.id == versionId);
            if (version) {
                this.setState({ version: version });
            }
        }

        if (!provider) {
            const { providers } = this.props.providerState;
            provider = providers.find(p => p.id == providerId);
            if (provider) {
                this.setState({ provider: provider });
            }
        }
    }

    public render(): ReactNode {
        const { groupId, boxId, versionId, providerId } = this.props.match.params;
        const { group, box, version, provider, openDeleteProviderConfirmModal, deleteProviderConfirmed } = this.state;
        const { groupState, boxState, versionState, providerState } = this.props;
        const { loading: groupLoading } = groupState;
        const { loading: boxLoading } = boxState;
        const { loading: versionLoading } = versionState;
        const { loading: providerLoading, modified } = providerState;

        if (groupLoading || boxLoading || versionLoading || providerLoading) {
            return <LoadingIndicator />;
        } else if (modified || deleteProviderConfirmed) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />
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
        } else if (!provider) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No provider found'
                content={`Could not find provider for ID ${providerId}`} />;
        } else {
            return <ViewProviderFragment
                group={group}
                box={box}
                version={version}
                provider={provider}
                onDeleteProviderButtonClick={this.onDeleteProviderButtonClick}
                openDeleteProviderConfirmModal={openDeleteProviderConfirmModal}
                onDeleteProviderModalClose={this.onDeleteProviderModalClose}
                onDeleteProviderModalCloseButtonClick={this.onDeleteProviderModalCloseButtonClick} />
        }
    };

    private onDeleteProviderButtonClick = () => {
        this.setState({ openDeleteProviderConfirmModal: true });
    };

    private onDeleteProviderModalClose = () => {
        this.setState({ openDeleteProviderConfirmModal: false });
    };

    private onDeleteProviderModalCloseButtonClick = (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => {
        const { providerId } = this.props.match.params;
        const { positive } = data;
        if (positive) {
            this.props.deleteProvider(providerId);
            this.setState({ openDeleteProviderConfirmModal: false, deleteProviderConfirmed: true });
        } else {
            this.setState({openDeleteProviderConfirmModal: false});
        }
    };
}

interface ViewProviderFragmentProps {
    group: Group;
    box: Box;
    version: Version;
    provider: Provider;
    onDeleteProviderButtonClick: () => void;
    openDeleteProviderConfirmModal: boolean;
    onDeleteProviderModalClose: (event: React.MouseEvent<HTMLElement>, data: ModalProps) => void;
    onDeleteProviderModalCloseButtonClick: (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => void;
}

const ViewProviderFragment: SFC<ViewProviderFragmentProps> = (props) => {
    const {
        group,
        box,
        version,
        provider,
        onDeleteProviderButtonClick,
        openDeleteProviderConfirmModal,
        onDeleteProviderModalClose,
        onDeleteProviderModalCloseButtonClick
    } = props;
    const { id: groupId, name: groupName } = group;
    const { id: boxId, name: boxName } = box;
    const { id: versionId, name: versionName } = version;
    const { id: providerId, providerType, size, checksumType, checksum } = provider;
    const formattedBytes = formatBytes(size, 0);
    const formattedChecksum = checksum ? `${checksumType}: ${checksum}` : undefined;

    return (
        <Container>
            <ConfirmModal
                title='Delete provider'
                subtitle='This action can not be reversed'
                open={openDeleteProviderConfirmModal}
                onClose={onDeleteProviderModalClose}
                onClick={onDeleteProviderModalCloseButtonClick} />
            <PrimaryHeader />
            <SecondaryHeader>
                <Link to='/'><Icon name='home' /></Link>{'/ '}
                <Link className="header-link" to={`/group/${groupId}`}>{groupName}</Link>{' / '}
                <Link className="header-link" to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>{' / '}
                <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}`}>{versionName}</Link>{' / '}
                <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}`}>{ProviderType[providerType]}</Link>
            </SecondaryHeader>
            <Segment basic>
                <Button.Group>
                    <Button primary size='small' onClick={onDeleteProviderButtonClick}>
                        <Icon name='delete' />Delete Provider
                    </Button>
                </Button.Group>
                <Table celled>
                    <Table.Body>
                        <Table.Row>
                            <Table.Cell width={2}><h3>Provider Type</h3></Table.Cell>
                            <Table.Cell width={8}>{providerType}</Table.Cell>
                        </Table.Row>
                        <Table.Row>
                            <Table.Cell width={2}><h3>Size</h3></Table.Cell>
                            <Table.Cell width={8}>{formattedBytes}</Table.Cell>
                        </Table.Row>
                        <Table.Row>
                            <Table.Cell width={2}><h3>Checksum</h3></Table.Cell>
                            <Table.Cell width={8}>{formattedChecksum}</Table.Cell>
                        </Table.Row>
                    </Table.Body>
                </Table>
            </Segment>
        </Container>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState,
    boxState: state.boxState,
    versionState: state.versionState,
    providerState: state.providerState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    deleteProvider: (providerId: number) => dispatch(deleteProvider(providerId)),
    getGroup: (groupId: number) => dispatch(getGroup(groupId)),
    getBox: (boxId: number) => dispatch(getBox(boxId)),
    getVersion: (versionId: number) => dispatch(getVersion(versionId)),
    getProvider: (providerId: number) => dispatch(getProvider(providerId))
});

const ConnectedProviderContainer = connect(mapStateToProps, mapDispatchToProps)(ProviderContainer);

export { ConnectedProviderContainer as ProviderContainer };