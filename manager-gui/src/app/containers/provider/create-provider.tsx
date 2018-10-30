import * as React from 'react';
import { ChangeEventHandler, Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import { Button, Container, Form, Header, Icon, InputOnChangeData, Message, Segment } from 'semantic-ui-react';

import { CreateProvider, ProviderState, RootState, ProviderType, ChecksumType } from '../../models';
import { findVersionProviders, createVersionProvider } from '../../state/actions';
import { LoadingIndicator, MainHeader } from '../../components';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    providerState: ProviderState;
}

interface ComponentDispatchProps {
    findVersionProviders: (versionId: number) => Promise<any>;
    createVersionProvider: (versionId: number, provider: CreateProvider) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps & RouteProps;

interface FormData {
    formError: boolean;
    formErrorMessage?: string;
    formProviderTypeValue: ProviderType;
    formChecksumTypeValue: ChecksumType;
    formChecksumValue: string;
    formSizeValue: number;
}

interface ComponentState {
    cancel: boolean;
    formData: FormData;
}

const initialState: ComponentState = {
    cancel: false,
    formData: {
        formError: false,
        formProviderTypeValue: ProviderType.VIRTUALBOX,
        formChecksumTypeValue: ChecksumType.SHA1,
        formChecksumValue: '',
        formSizeValue: 0
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
        const { versionId, boxId, groupId } = this.props.match.params;
        const { cancel, formData } = this.state;
        const { providerState } = this.props;
        const { loading } = providerState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else {
            return <CreateProviderFragment
                onCancelButtonClick={this.onCancelButtonClick}
                onFormSubmit={this.onFormSubmit}
                onFormProviderTypeChange={this.onFormProviderTypeChange}
                onFormChecksumTypeChange={this.onFormChecksumTypeChange}
                onFormSizeChange={this.onFormSizeChange}
                onFormChecksumChange={this.onFormChecksumChange}
                formData={formData} />
        }
    }

    private onFormSubmit = () => {
        const { versionId } = this.props.match.params;
        const { formData } = this.state;
        const { formProviderTypeValue, formChecksumTypeValue, formChecksumValue, formSizeValue } = formData;
  
        this.props.createVersionProvider(versionId, { 
                    provider_type: formProviderTypeValue,
                    checksum_type: formChecksumTypeValue,
                    checksum: formChecksumValue,
                    size: formSizeValue
                });
        
    };

    private onFormProviderTypeChange: ChangeEventHandler<HTMLSelectElement> = (event) => {
        const { value } = event.currentTarget;
        const { formData } = this.state;
        this.setState({ formData: { ...formData, formError: false, formWarning: false, formProviderTypeValue: ProviderType[value] } });
    }

    private onFormChecksumTypeChange: ChangeEventHandler<HTMLSelectElement> = (event) => {
        const { value } = event.currentTarget;
        const { formData } = this.state;
        this.setState({ formData: { ...formData, formError: false, formWarning: false, formChecksumTypeValue: ChecksumType[value] } });
    }

    private onFormSizeChange: ChangeEventHandler<HTMLInputElement> = (event) => {
        const { value } = event.currentTarget;
        const { formData } = this.state;
        this.setState({ formData: { ...formData, formError: false, formWarning: false, formSizeValue: Number(value) } });
    }
    
    private onFormChecksumChange: ChangeEventHandler<HTMLInputElement> = (event) => {
        const { value } = event.currentTarget;
        const { formData } = this.state;
        this.setState({ formData: { ...formData, formError: false, formWarning: false, formChecksumValue: value } });
    }

    private onCancelButtonClick = () => {
        this.setState({ cancel: true });
    };
}

interface CreateProviderFragmentProps {
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    onFormProviderTypeChange: (event: React.SyntheticEvent<HTMLSelectElement>, data: InputOnChangeData) => void;
    onFormChecksumTypeChange: (event: React.SyntheticEvent<HTMLSelectElement>, data: InputOnChangeData) => void;
    onFormSizeChange: (event: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => void;
    onFormChecksumChange: (event: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => void;

    formData: FormData;
};

const CreateProviderFragment: SFC<CreateProviderFragmentProps> = (props) => {
    const { 
        onCancelButtonClick, 
        onFormSubmit, 
        onFormProviderTypeChange, 
        onFormChecksumTypeChange,
        onFormSizeChange,
        onFormChecksumChange,
        formData
    } = props;
    
    const {
        formError,
        formErrorMessage,
        formProviderTypeValue,
        formChecksumTypeValue,
        formChecksumValue,
        formSizeValue
    } = formData;

    let providerTypeOptionsMap: { key: string, text: string, value: string }[] = [];
    for (var pt in ProviderType) {
        providerTypeOptionsMap.push({key: ProviderType[pt], text: ProviderType[pt], value: ProviderType[pt]})
    }
    const providerTypeOptions = providerTypeOptionsMap;
    let checksumTypeOptionsMap: { key: string, text: string, value: string }[] = [];
    for (var ct in ChecksumType) {
        checksumTypeOptionsMap.push({key: ChecksumType[ct], text: ChecksumType[ct], value: ChecksumType[ct]})
    }
    const checksumTypeOptions = checksumTypeOptionsMap;
    
    return (
        <Container>
            <MainHeader />
            <Segment basic>
                <Header>Create Provider</Header>
            </Segment>
            <Segment basic>
                <Form onSubmit={onFormSubmit} error={formError}>
                    <Form.Group>
                        <Form.Select 
                            width={5} 
                            label='Provider Type'
                            placeholder='Select provider type...'
                            options={providerTypeOptions}
                            value={formProviderTypeValue}
                            onChange={onFormProviderTypeChange}/>
                    </Form.Group>
                    <Form.Group>
                        <Form.Select 
                            width={5} 
                            label='Checksum Type'
                            placeholder='Select checksum type...'
                            options={checksumTypeOptions}
                            value={formChecksumTypeValue}
                            onChange={onFormChecksumTypeChange}/>
                    </Form.Group>
                    <Form.Group>
                        <Form.Input
                            error={formError}
                            width={10}
                            placeholder='Enter checksum...'
                            label='Checksum'
                            value={formChecksumValue}
                            onChange={onFormChecksumChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Input
                            error={formError}
                            width={2}
                            placeholder='Enter size...'
                            label='Size'
                            value={formSizeValue}
                            onChange={onFormSizeChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Button
                            primary size='tiny'><Icon name='cube' />Create Provider</Form.Button>
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
    createVersionProvider: (versionId: number, provider: CreateProvider) => dispatch(createVersionProvider(versionId, provider))
});

const ConnectedCreateProviderContainer = connect(mapStateToProps, mapDispatchToProps)(CreateProviderContainer);

export { ConnectedCreateProviderContainer as CreateProviderContainer };