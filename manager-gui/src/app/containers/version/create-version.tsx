import * as React from 'react';
import { ChangeEventHandler, Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import { Button, Container, Form, Header, Icon, InputOnChangeData, TextAreaProps, Message, Segment } from 'semantic-ui-react';

import { CreateVersion, VersionState, RootState } from '../../models';
import { createBoxVersion, findBoxVersions } from '../../state/actions';
import { LoadingIndicator, MainHeader } from '../../components';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    versionState: VersionState;
}

interface ComponentDispatchProps {
    findBoxVersions: (boxId: number) => Promise<any>;
    createBoxVersion: (boxId: number, version: CreateVersion) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps & RouteProps;

interface FormData {
    formError: boolean;
    formErrorMessage?: string;
    formNameValue: string;
    formDescriptionValue?: string;
}

interface ComponentState {
    cancel: boolean;
    formData: FormData;
}

const initialState: ComponentState = {
    cancel: false,
    formData: {
        formError: false,
        formNameValue: ''
    }
};

class CreateVersionContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { boxId } = this.props.match.params;
        this.props.findBoxVersions(boxId);
    }

    public render(): ReactNode {
        const { boxId, groupId } = this.props.match.params;
        const { cancel, formData } = this.state;
        const { versionState } = this.props;
        const { loading } = versionState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}/box/${boxId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else {
            return <CreateBoxFragment
                onCancelButtonClick={this.onCancelButtonClick}
                onFormSubmit={this.onFormSubmit}
                onFormInputChange={this.onFormInputChange}
                onFormTextAreaChange={this.onFormTextAreaChange}
                formData={formData} />
        }
    }

    private onFormSubmit = () => {
        const { boxId } = this.props.match.params;
        const { formData } = this.state;
        const { formNameValue, formDescriptionValue } = formData;
        if (!formNameValue || formNameValue.length < 3) {
            this.setState({ formData: { ...formData, formError: true, formErrorMessage: 'Version name must be at least 3 letters long' } });
        } else if (/\s/.test(formNameValue)) {
            this.setState({ formData: { ...formData, formError: true, formErrorMessage: 'Version name cannot contain any spaces'}})
        } else {
            this.props.createBoxVersion(boxId, { name: formNameValue, description: formDescriptionValue });
        }
    };

    private onFormInputChange: ChangeEventHandler<HTMLInputElement> = (event) => {
        const { value } = event.currentTarget;
        const { formData } = this.state;
        this.setState({ formData: { ...formData, formError: false, formWarning: false, formNameValue: value } });
    }

    private onFormTextAreaChange: ChangeEventHandler<HTMLTextAreaElement> = (event) => {
        const { value } = event.currentTarget;
        const { formData } = this.state;
        this.setState({ formData: { ...formData, formError: false, formWarning: false, formDescriptionValue: value } });
    }

    private onCancelButtonClick = () => {
        this.setState({ cancel: true });
    };
}

interface CreateBoxFragmentProps {
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    onFormInputChange: (event: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => void;
    onFormTextAreaChange: (event: React.SyntheticEvent<HTMLTextAreaElement>, data: TextAreaProps) => void;
    formData: FormData;
};

const CreateBoxFragment: SFC<CreateBoxFragmentProps> = (props) => {
    const { onCancelButtonClick, onFormSubmit, onFormInputChange, onFormTextAreaChange, formData } = props;
    const {
        formError,
        formErrorMessage,
        formNameValue,
        formDescriptionValue
    } = formData;

    return (
        <Container>
            <MainHeader />
            <Segment basic>
                <Header>Create Version</Header>
            </Segment>
            <Segment basic>
                <Form onSubmit={onFormSubmit} error={formError}>
                    <Form.Group>
                        <Form.Input
                            error={formError}
                            width={10}
                            placeholder='Enter version name...'
                            label='Version Name'
                            value={formNameValue}
                            onChange={onFormInputChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.TextArea
                            width={10}
                            placeholder='Enter version description...'
                            label='Version Description'
                            value={formDescriptionValue} 
                            onChange={onFormTextAreaChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Button
                            primary size='tiny'><Icon name='cube' />Create Version</Form.Button>
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
    versionState: state.versionState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findBoxVersions: (boxId: number) => dispatch(findBoxVersions(boxId)),
    createBoxVersion: (boxId: number, version: CreateVersion) => dispatch(createBoxVersion(boxId, version))
});

const ConnectedCreateVersionContainer = connect(mapStateToProps, mapDispatchToProps)(CreateVersionContainer);

export { ConnectedCreateVersionContainer as CreateVersionContainer };