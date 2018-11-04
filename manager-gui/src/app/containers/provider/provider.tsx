import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import {
    Button,
    Container,
    Form,
    Icon,
    Message,
    DropdownItemProps,
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
import { getBox, getGroup, getProvider, getVersion, updateVersionProvider } from '../../state/actions';
import { LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';
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
    getGroup: (groupId: number) => Promise<any>;
    getBox: (boxId: number) => Promise<any>;
    getVersion: (versionId: number) => Promise<any>;
    getProvider: (providerId: number) => Promise<any>;
    updateVersionProvider: (versionId: number, file: any) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps & RouteProps;

interface FormData {
    formError: boolean;
    formErrorMessage?: string;
    formWarning: boolean;
    formWarningMessage?: string;
    formFile?: File;
}

interface ComponentState {
    group?: Group;
    box?: Box;
    version?: Version;
    provider?: Provider;
    cancel: boolean;
    formData: FormData;
}

const initialState: ComponentState = {
    cancel: false,
    formData: {
        formError: false,
        formWarning: false
    }
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
        const { group, box, version, provider, cancel, formData } = this.state;
        const { groupState, boxState, versionState, providerState } = this.props;
        const { loading: groupLoading } = groupState;
        const { loading: boxLoading } = boxState;
        const { loading: versionLoading } = versionState;
        const { loading: providerLoading } = providerState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />;
        } else if (groupLoading || boxLoading || versionLoading || providerLoading) {
            return <LoadingIndicator />;
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
        } else if (provider.size && provider.checksum) {
            return <ViewProviderFragment
                group={group}
                box={box}
                version={version}
                provider={provider} />
        } else {
            return <UpdateProviderFragment
                group={group}
                box={box}
                version={version}
                provider={provider}
                onFormInputChange={this.onFormInputChange}
                onCancelButtonClick={this.onCancelButtonClick}
                onFormSubmit={this.onFormSubmit}
                formData={formData} />
        }
    }

    private onFormInputChange = (event: any) => {
        const { formData } = this.state;
        const { files } = event.target;
        if (files && files.length > 0) {
            this.setState({
                formData: {
                    ...formData,
                    formError: false,
                    formWarning: false,
                    formFile: files[0]
                }
            });
        } else {
            this.setState({
                formData: {
                    ...formData,
                    formError: false,
                    formWarning: true,
                    formWarningMessage: 'No file found in input'
                }
            });
        }
    }

    private onFormSubmit = (event: any) => {
        const { providerId } = this.props.match.params;
        const { formData } = this.state;
        const { formFile } = formData;
        if (formFile) {
            this.props.updateVersionProvider(providerId, formFile);
        } else {
            this.setState({
                formData: {
                    ...formData,
                    formError: false,
                    formWarning: true,
                    formWarningMessage: 'No file selected'
                }
            });
        }
    }

    private onCancelButtonClick = () => {
        this.setState({ cancel: true });
    }
}

interface ViewProviderFragmentProps {
    group: Group;
    box: Box;
    version: Version;
    provider: Provider;
};

const ViewProviderFragment: SFC<ViewProviderFragmentProps> = (props) => {
    const {
        group,
        box,
        version,
        provider,
    } = props;
    const { id: groupId, name: groupName } = group;
    const { id: boxId, name: boxName } = box;
    const { id: versionId, name: versionName } = version;
    const { id: providerId, providerType, size, checksumType, checksum } = provider;

    return (
        <Container>
            <PrimaryHeader />
            <SecondaryHeader>
                <Link to='/'><Icon name='home' /></Link>{'/ '}
                <Link className="header-link" to={`/group/${groupId}`}>{groupName}</Link>{' / '}
                <Link className="header-link" to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>{' / '}
                <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}`}>{versionName}</Link>{' / '}
                <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}`}>{ProviderType[providerType]}</Link>
            </SecondaryHeader>
            <Segment basic>
                <Table celled>
                    <Table.Body>
                        <Table.Row>
                            <Table.Cell><h3>Provider Type</h3></Table.Cell>
                            <Table.Cell>{providerType}</Table.Cell>
                        </Table.Row>
                        <Table.Row>
                            <Table.Cell><h3>Size</h3></Table.Cell>
                            <Table.Cell>{formatBytes(size, 0)}</Table.Cell>
                        </Table.Row>
                        <Table.Row>
                            <Table.Cell><h3>Checksum</h3></Table.Cell>
                            <Table.Cell>{checksumType}: {checksum}</Table.Cell>
                        </Table.Row>
                    </Table.Body>
                </Table>
            </Segment>
        </Container>
    );
}

interface UpdateProviderFragmentProps {
    group: Group;
    box: Box;
    version: Version;
    provider: Provider;
    onFormInputChange: (event: any) => void;
    onCancelButtonClick: () => void;
    onFormSubmit: (event: any) => void
    formData: FormData;
};

const UpdateProviderFragment: SFC<UpdateProviderFragmentProps> = (props) => {
    const {
        group,
        box,
        version,
        provider,
        onFormInputChange,
        onCancelButtonClick,
        onFormSubmit,
        formData
    } = props;
    const {
        formError,
        formErrorMessage,
        formWarning,
        formWarningMessage
    } = formData;
    const { id: groupId, name: groupName } = group;
    const { id: boxId, name: boxName } = box;
    const { id: versionId, name: versionName } = version;
    const { id: providerId, providerType } = provider;

    const providerTypeOptions: DropdownItemProps[] = [];
    Object.keys(ProviderType)
        .map(key => ProviderType[key])
        .forEach(type => providerTypeOptions
            .push({ key: type, text: type, value: type }));

    return (
        <Container>
            <PrimaryHeader />
            <SecondaryHeader>
                <Link to='/'><Icon name='home' /></Link>{'/ '}
                <Link className="header-link" to={`/group/${groupId}`}>{groupName}</Link>{' / '}
                <Link className="header-link" to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>{' / '}
                <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}`}>{versionName}</Link>{' / '}
                <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}`}>{ProviderType[providerType]}</Link>{' / '}
                Upload File
                </SecondaryHeader>
            <Segment basic>
                <Form onSubmit={onFormSubmit} error={formError} warning={formWarning}>
                    <Form.Group>
                        <Form.Input
                            type='file'
                            error={formError}
                            width={10}
                            label='Provider File'
                            placeholder='Enter provider file...'
                            onChange={onFormInputChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Button
                            primary size='tiny'><Icon name='file' />Upload Provider</Form.Button>
                        <Button
                            secondary size='tiny'
                            onClick={onCancelButtonClick}><Icon name='cancel' />Cancel</Button>
                    </Form.Group>
                    <Message error><Icon name='ban' /> {formErrorMessage}</Message>
                    <Message warning><Icon name='warning sign' /> {formWarningMessage}</Message>
                </Form>
            </Segment>
        </Container>
    );
}

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    groupState: state.groupState,
    boxState: state.boxState,
    versionState: state.versionState,
    providerState: state.providerState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    getGroup: (groupId: number) => dispatch(getGroup(groupId)),
    getBox: (boxId: number) => dispatch(getBox(boxId)),
    getVersion: (versionId: number) => dispatch(getVersion(versionId)),
    getProvider: (providerId: number) => dispatch(getProvider(providerId)),
    updateVersionProvider: (versionId: number, file: any) => dispatch(updateVersionProvider(versionId, file))
});

const ConnectedProviderContainer = connect(mapStateToProps, mapDispatchToProps)(ProviderContainer);

export { ConnectedProviderContainer as ProviderContainer };