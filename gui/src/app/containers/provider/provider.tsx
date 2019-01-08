import * as React from 'react';
import { Component, FunctionComponent, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
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
    Provider,
    ProviderState,
    ProviderType,
    RootState
} from '../../models';
import {
    deleteProvider,
    getProvider,
} from '../../state/actions';
import { ConfirmModal, LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';
import { NotFoundErrorContainer } from '..';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    providerState: ProviderState;
}

interface ComponentDispatchProps {
    deleteProvider: (providerId: number) => Promise<any>;
    getProvider: (providerId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps & RouteProps;

interface ComponentState {
    provider?: Provider;
    editProvider: boolean;
    openDeleteProviderConfirmModal: boolean;
    deleteProviderConfirmed: boolean;
}

const initialState: ComponentState = {
    editProvider: false,
    openDeleteProviderConfirmModal: false,
    deleteProviderConfirmed: false
};

class ProviderContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { providerId } = this.props.match.params;
        providerId && this.props.getProvider(providerId);
    }

    componentDidUpdate() {
        const { providerId } = this.props.match.params;
        let { provider } = this.state;

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
        const {
            provider,
            editProvider,
            openDeleteProviderConfirmModal,
            deleteProviderConfirmed
        } = this.state;
        const { loading, modified } = this.props.providerState;

        if (loading) {
            return <LoadingIndicator />;
        } else if (modified || deleteProviderConfirmed) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />
        } else if (editProvider) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}/edit`} />
        } else if (!provider) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No provider found'
                content={`Could not find provider for ID ${providerId}`} />;
        } else {
            return <ProviderFragment
                provider={provider}
                onEditProviderButtonClick={this.onEditProviderButtonClick}
                onDeleteProviderButtonClick={this.onDeleteProviderButtonClick}
                openDeleteProviderConfirmModal={openDeleteProviderConfirmModal}
                onDeleteProviderModalClose={this.onDeleteProviderModalClose}
                onDeleteProviderModalCloseButtonClick={this.onDeleteProviderModalCloseButtonClick} />
        }
    };

    private onEditProviderButtonClick = () => {
        this.setState({ editProvider: true });
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
            this.setState({ openDeleteProviderConfirmModal: false });
        }
    };
}

interface ProviderFragmentProps {
    provider: Provider;
    onEditProviderButtonClick: () => void;
    onDeleteProviderButtonClick: () => void;
    openDeleteProviderConfirmModal: boolean;
    onDeleteProviderModalClose: (event: React.MouseEvent<HTMLElement>, data: ModalProps) => void;
    onDeleteProviderModalCloseButtonClick: (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => void;
}

const ProviderFragment: FunctionComponent<ProviderFragmentProps> = (props) => {
    const {
        provider,
        onEditProviderButtonClick,
        onDeleteProviderButtonClick,
        openDeleteProviderConfirmModal,
        onDeleteProviderModalClose,
        onDeleteProviderModalCloseButtonClick
    } = props;
    const { version } = provider;
    const { id: versionId, name: versionName, box } = version;
    const { id: boxId, name: boxName, group } = box;
    const { id: groupId, name: groupName } = group;
    const { id: providerId, providerType, size, checksumType, checksum } = provider;
    const formattedBytes = formatBytes(size, 0);
    const formattedChecksum = checksum ? `${checksumType}: ${checksum}` : undefined;

    return (
        <Container>
            <ConfirmModal
                title='Delete Provider'
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
                    <Button primary size='tiny' onClick={onEditProviderButtonClick}>
                        <Icon name='pencil' />Edit Provider
                    </Button>
                </Button.Group>
                <Button.Group>
                    <Button primary size='tiny' onClick={onDeleteProviderButtonClick}>
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
    providerState: state.providerState,
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    deleteProvider: (providerId: number) => dispatch(deleteProvider(providerId)),
    getProvider: (providerId: number) => dispatch(getProvider(providerId))
});

const ConnectedProviderContainer = connect(mapStateToProps, mapDispatchToProps)(ProviderContainer);

export { ConnectedProviderContainer as ProviderContainer };