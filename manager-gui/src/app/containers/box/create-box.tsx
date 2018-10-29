import * as React from 'react';
import { ChangeEventHandler, Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import { Button, Container, Form, Header, Icon, InputOnChangeData, Message, Segment } from 'semantic-ui-react';

import { CreateBox, BoxState, RootState } from '../../models';
import { createGroupBox, findGroupBoxes } from '../../state/actions';
import { LoadingIndicator, MainHeader } from '../../components';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    boxState: BoxState;
}

interface ComponentDispatchProps {
    findGroupBoxes: (groupId: number) => Promise<any>;
    createGroupBox: (groupId: number, box: CreateBox) => Promise<any>;
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

class CreateBoxContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId } = this.props.match.params;
        this.props.findGroupBoxes(groupId);
    }

    public render(): ReactNode {
        const { groupId } = this.props.match.params;
        const { cancel, formData } = this.state;
        const { boxState } = this.props;
        const { loading } = boxState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else {
            return <CreateBoxFragment
                onCancelButtonClick={this.onCancelButtonClick}
                onFormSubmit={this.onCreateGroupFormSubmit}
                onFormInputChange={this.onFormInputChange}
                formData={formData} />
        }
    }

    private onCreateGroupFormSubmit = () => {
        const { groupId } = this.props.match.params;
        const { formData } = this.state;
        const { formNameValue, formDescriptionValue } = formData;
        if (!formNameValue || formNameValue.length < 3) {
            this.setState({ formData: { ...formData, formError: true, formErrorMessage: 'Box name must be atleast 3 letters long' } });
        } else {
            this.props.createGroupBox(groupId, { name: formNameValue, description: formDescriptionValue });
        }
    };

    private onFormInputChange: ChangeEventHandler<HTMLInputElement> = (event) => {
        const { value } = event.currentTarget;
        const { formData } = this.state;
        this.setState({ formData: { ...formData, formError: false, formWarning: false, formNameValue: value } });
    }

    private onCancelButtonClick = () => {
        this.setState({ cancel: true });
    };
}

interface CreateBoxFragmentProps {
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    onFormInputChange: (event: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => void;
    formData: FormData;
};

const CreateBoxFragment: SFC<CreateBoxFragmentProps> = (props) => {
    const { onCancelButtonClick, onFormSubmit, onFormInputChange, formData } = props;
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
                <Header>Create Box</Header>
            </Segment>
            <Segment basic>
                <Form onSubmit={onFormSubmit} error={formError}>
                    <Form.Group>
                        <Form.Input
                            error={formError}
                            width={10}
                            placeholder='Enter box name...'
                            label='Box Name'
                            value={formNameValue}
                            onChange={onFormInputChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.TextArea
                            width={10}
                            placeholder='Enter box description...'
                            label='Box Description'
                            value={formDescriptionValue} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Button
                            primary size='tiny'><Icon name='cube' />Create Box</Form.Button>
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
    boxState: state.boxState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    findGroupBoxes: (groupId: number) => dispatch(findGroupBoxes(groupId)),
    createGroupBox: (groupId: number, box: CreateBox) => dispatch(createGroupBox(groupId, box))
});

const ConnectedCreateBoxContainer = connect(mapStateToProps, mapDispatchToProps)(CreateBoxContainer);

export { ConnectedCreateBoxContainer as CreateBoxContainer };