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
    Segment,
} from 'semantic-ui-react';

import {
    File,
    FileState,
    Provider,
    ProviderState,
    ProviderType,
    RootState,
    ActionType
} from '../../models';
import {
    findFiles,
    getProvider,
    updateProvider,
    showWarning
} from '../../state/actions';
import { LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';
import { Link } from 'react-router-dom';
import { NotFoundErrorContainer } from '../error';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    providerState: ProviderState;
    fileState: FileState;
}

interface ComponentDispatchProps {
    findFiles: (providerId: number) => Promise<any>;
    getProvider: (providerId: number) => Promise<any>;
    updateProvider: (providerId: number, file: any) => Promise<any>;
    showWarning: (title: string, content: string) => Promise<any>;
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
    provider?: Provider;
    files?: File[];
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

class EditProviderContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { providerId } = this.props.match.params;
        providerId && this.props.getProvider(providerId);
        providerId && this.props.findFiles(providerId);
    }

    componentDidUpdate() {
        const { providerId } = this.props.match.params;
        let { provider, files } = this.state;

        if (!provider) {
            const { providers } = this.props.providerState;
            provider = providers.find(p => p.id == providerId);
            if (provider) {
                this.setState({ provider: provider });
            }
        }

        if (!files) {
            const { files: existingFiles } = this.props.fileState;
            files = existingFiles.filter(f => f.providerId == providerId);
            if (files && files.length) {
                this.setState({ files: files });
            }
        }

        if (this.showExistingFileNotification()) {
            this.props.showWarning(
                'Provider file exists',
                'There already exists a provider file for given provider. ' +
                'Please delete provider in order to upload new provider file.');
        }
    }

    public render(): ReactNode {
        const { groupId, boxId, versionId, providerId } = this.props.match.params;
        const { provider, files, cancel, formData } = this.state;
        const { loading: providerLoading, modified } = this.props.providerState;
        const { loading: filesLoading } = this.props.fileState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}`} />;
        } else if (modified && modified.actionType == ActionType.UPDATE) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}`} />;
        } else if (providerLoading || filesLoading) {
            return <LoadingIndicator />;
        } else if (!provider) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No provider found'
                content={`Could not find provider for ID ${providerId}`} />;
        } else {
            return <EditProviderFragment
                provider={provider}
                files={files}
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
    };

    private onFormSubmit = () => {
        const { providerId } = this.props.match.params;
        const { formData } = this.state;
        const { formFile } = formData;
        if (formFile) {
            this.props.updateProvider(providerId, formFile);
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
    };

    private onCancelButtonClick = () => {
        this.setState({ cancel: true });
    }

    private showExistingFileNotification = () => {
        const { loading: providerLoading } = this.props.providerState;
        const { loading: filesLoading } = this.props.fileState;
        const { provider, files } = this.state;
        if (!providerLoading && !filesLoading && files && files.length && provider) {
            const { size, checksumType, checksum } = provider;
            return !size || !checksumType || !checksum;
        } else {
            return false;
        }
    }
}

interface EditProviderFragmentProps {
    provider: Provider;
    files?: File[];
    onFormInputChange: (event: any) => void;
    onCancelButtonClick: () => void;
    onFormSubmit: (event: any) => void
    formData: FormData;
}

const EditProviderFragment: SFC<EditProviderFragmentProps> = (props) => {
    const {
        provider,
        files,
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
    const { id: providerId, providerType, version } = provider;
    const { id: versionId, name: versionName, box } = version;
    const { id: boxId, name: boxName, group } = box;
    const { id: groupId, name: groupName } = group;

    if (files && files.length) {
        return (
            <Container>
                <PrimaryHeader />
                <SecondaryHeader>
                    <Link to='/'><Icon name='home' /></Link>{'/ '}
                    <Link className="header-link" to={`/group/${groupId}`}>{groupName}</Link>{' / '}
                    <Link className="header-link" to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>{' / '}
                    <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}`}>{versionName}</Link>{' / '}
                    <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}`}>{ProviderType[providerType]}</Link>{' / '}
                    <i>Edit Provider</i>
                </SecondaryHeader>
                <Segment basic>
                    <Button.Group>
                        <Button primary size='tiny' onClick={onCancelButtonClick}>
                            <Icon name='arrow left' />Back
                        </Button>
                    </Button.Group>
                </Segment>
            </Container>
        );
    } else {
        return (
            <Container>
                <PrimaryHeader />
                <SecondaryHeader>
                    <Link to='/'><Icon name='home' /></Link>{'/ '}
                    <Link className="header-link" to={`/group/${groupId}`}>{groupName}</Link>{' / '}
                    <Link className="header-link" to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>{' / '}
                    <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}`}>{versionName}</Link>{' / '}
                    <Link className="header-link" to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}`}>{ProviderType[providerType]}</Link>{' / '}
                    <i>Edit Provider</i>
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
                                primary size='tiny'><Icon name='file' />Update Provider</Form.Button>
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
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    providerState: state.providerState,
    fileState: state.fileState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findFiles: (providerId: number) => dispatch(findFiles(providerId)),
    getProvider: (providerId: number) => dispatch(getProvider(providerId)),
    updateProvider: (providerId: number, file: any) => dispatch(updateProvider(providerId, file)),
    showWarning: (title: string, content: string) => dispatch(showWarning({ title: title, content: content }))
});

const ConnectedEditProviderContainer = connect(mapStateToProps, mapDispatchToProps)(EditProviderContainer);

export { ConnectedEditProviderContainer as EditProviderContainer };