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
    Segment
} from 'semantic-ui-react';

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
        this.props.getGroup(groupId);
        this.props.getBox(boxId);
        this.props.getVersion(versionId);
        this.props.getProvider(providerId);
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
        const { groupId, boxId, versionId } = this.props.match.params;
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
        } else {
            return <UploadBoxFragment
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

interface CreateBoxFragmentProps {
    group?: Group;
    box?: Box;
    version?: Version;
    provider?: Provider;
    onFormInputChange: (event: any) => void;
    onCancelButtonClick: () => void;
    onFormSubmit: (event: any) => void
    formData: FormData;
};

const UploadBoxFragment: SFC<CreateBoxFragmentProps> = (props) => {
    const {
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

    const providerTypeOptions: DropdownItemProps[] = [];
    Object.keys(ProviderType)
        .map(key => ProviderType[key])
        .forEach(type => providerTypeOptions
            .push({ key: type, text: type, value: type }));

    return (
        <Container>
            <PrimaryHeader />
            <SecondaryHeader>
                Upload Provider File
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