import * as React from 'react';
import { Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import { Button, Container, Form, Icon, Message, DropdownItemProps, Segment } from 'semantic-ui-react';

import { ProviderState, ProviderType, RootState } from '../../models';
import { uploadVersionProvider, findVersionProviders } from '../../state/actions';
import { LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    providerState: ProviderState;
}

interface ComponentDispatchProps {
    findVersionProviders: (versionId: number) => Promise<any>;
    uploadVersionProvider: (versionId: number) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps & RouteProps;

interface FormData {
    formError: boolean;
    formErrorMessage?: string;
}

interface ComponentState {
    cancel: boolean;
    formData: FormData;
}

const initialState: ComponentState = {
    cancel: false,
    formData: {
        formError: false
    }
};

class UploadProviderContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { versionId } = this.props.match.params;
        this.props.findVersionProviders(versionId);
    }

    public render(): ReactNode {
        const { boxId, groupId, versionId } = this.props.match.params;
        const { cancel, formData } = this.state;
        const { providerState } = this.props;
        const { loading, createSuccess } = providerState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (createSuccess) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />;
        } else {
            return <UploadBoxFragment
                onCancelButtonClick={this.onCancelButtonClick}
                onFormSubmit={this.onFormSubmit}
                formData={formData} />
        }
    }

    private onFormSubmit = () => {
        const { versionId } = this.props.match.params;
        this.props.uploadVersionProvider(versionId);
    }

    private onCancelButtonClick = () => {
        this.setState({ cancel: true });
    }
}

interface CreateBoxFragmentProps {
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    formData: FormData;
};

const UploadBoxFragment: SFC<CreateBoxFragmentProps> = (props) => {
    const { onCancelButtonClick, onFormSubmit, formData } = props;
    const {
        formError,
        formErrorMessage,
    } = formData;
    const providerTypeOptions: DropdownItemProps[] = [];
    Object.keys(ProviderType)
        .map(key => ProviderType[key])
        .forEach(type => providerTypeOptions
            .push({ key: type, text: type, value: type }));

    return (
        <Container>
            <PrimaryHeader />
            <SecondaryHeader>Upload Provider</SecondaryHeader>
            <Segment basic>
                <Form onSubmit={onFormSubmit} error={formError}>
                    <Form.Group>
                        <Form.Button
                            primary size='tiny'><Icon name='file' />Upload Provider</Form.Button>
                        <Button
                            secondary size='tiny'
                            onClick={onCancelButtonClick}><Icon name='cancel' />Cancel</Button>
                    </Form.Group>
                    <Message error><Icon name='ban' /> {formErrorMessage}</Message>
                </Form>
            </Segment>
        </Container>
    );
}

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    providerState: state.providerState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findVersionProviders: (versionId: number) => dispatch(findVersionProviders(versionId)),
    uploadVersionProvider: (versionId: number) => dispatch(uploadVersionProvider(versionId))
});

const ConnectedUploadProviderContainer = connect(mapStateToProps, mapDispatchToProps)(UploadProviderContainer);

export { ConnectedUploadProviderContainer as UploadProviderContainer };