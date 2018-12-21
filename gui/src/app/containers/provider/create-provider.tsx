import * as React from 'react';
import { ChangeEventHandler, Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import {
    Button,
    Container,
    DropdownProps,
    Form,
    Icon,
    Message,
    DropdownItemProps,
    Segment
} from 'semantic-ui-react';

import {
    ModifyProvider,
    ProviderState,
    ProviderType,
    RootState,
    ActionType
} from '../../models';
import { createVersionProvider, findVersionProviders } from '../../state/actions';
import { LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    providerState: ProviderState;
}

interface ComponentDispatchProps {
    findVersionProviders: (versionId: number) => Promise<any>;
    createVersionProvider: (versionId: number, provider: ModifyProvider) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps & RouteProps;

interface FormData {
    formError: boolean;
    formErrorMessage?: string;
    providerType: ProviderType;
}

interface ComponentState {
    cancel: boolean;
    formData: FormData;
}

const initialState: ComponentState = {
    cancel: false,
    formData: {
        formError: false,
        providerType: ProviderType.VIRTUALBOX
    }
};

class CreateProviderContainer extends Component<ComponentProps, ComponentState> {

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
        const { loading, modified } = providerState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (modified && modified.actionType == ActionType.CREATE) {
            const { id: providerId } = modified;
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}/provider/${providerId}/update`} />;
        } else {
            return <CreateBoxFragment
                onCancelButtonClick={this.onCancelButtonClick}
                onFormSubmit={this.onFormSubmit}
                onFormSelectChange={this.onFormSelectChange}
                formData={formData} />
        }
    }

    private onFormSubmit = () => {
        const { versionId } = this.props.match.params;
        const { formData } = this.state;
        const { providerType } = formData;
        this.props.createVersionProvider(versionId, { providerType: providerType });
    };

    private onFormSelectChange: ChangeEventHandler<HTMLInputElement> = (event) => {
        const { value } = event.currentTarget;
        const providerType = ProviderType[value];
        const { formData } = this.state;
        this.setState({ formData: { ...formData, formError: false, formWarning: false, providerType: providerType } });
    };

    private onCancelButtonClick = () => {
        this.setState({ cancel: true });
    };
}

interface CreateBoxFragmentProps {
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    onFormSelectChange: (event: React.SyntheticEvent<HTMLInputElement>, data: DropdownProps) => void;
    formData: FormData;
}

const CreateBoxFragment: SFC<CreateBoxFragmentProps> = (props) => {
    const { onCancelButtonClick, onFormSubmit, onFormSelectChange, formData } = props;
    const {
        formError,
        formErrorMessage,
        providerType
    } = formData;
    const providerTypeOptions: DropdownItemProps[] = [];
    Object.keys(ProviderType)
        .map(key => ProviderType[key])
        .forEach(type => providerTypeOptions
            .push({ key: type, text: type, value: type }));

    return (
        <Container>
            <PrimaryHeader />
            <SecondaryHeader>Create Provider</SecondaryHeader>
            <Segment basic>
                <Form onSubmit={onFormSubmit} error={formError}>
                    <Form.Group>
                        <Form.Select
                            error={formError}
                            width={10}
                            label='Provider Type'
                            options={providerTypeOptions}
                            value={providerType}
                            onChange={onFormSelectChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Button
                            primary size='tiny'><Icon name='file' />Create Provider</Form.Button>
                        <Button
                            secondary size='tiny'
                            onClick={onCancelButtonClick}><Icon name='cancel' />Cancel</Button>
                    </Form.Group>
                    <Message error><Icon name='ban' /> {formErrorMessage}</Message>
                </Form>
            </Segment>
        </Container>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    providerState: state.providerState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findVersionProviders: (versionId: number) => dispatch(findVersionProviders(versionId)),
    createVersionProvider: (versionId: number, provider: ModifyProvider) => dispatch(createVersionProvider(versionId, provider))
});

const ConnectedCreateProviderContainer = connect(mapStateToProps, mapDispatchToProps)(CreateProviderContainer);

export { ConnectedCreateProviderContainer as CreateProviderContainer };