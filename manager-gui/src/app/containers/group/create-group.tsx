import * as React from 'react';
import { ChangeEventHandler, Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import { Button, Container, Form, Header, Icon, InputOnChangeData, Message, Segment } from 'semantic-ui-react';

import { CreateGroup, GroupState, RootState } from '../../models';
import { createGroup } from '../../state/actions';
import { LoadingIndicator, MainHeader } from '../../components';

interface ComponentStateProps {
    groupState: GroupState;
}

interface ComponentDispatchProps {
    createGroup: (createGroup: CreateGroup) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps;

interface FormData {
    formError: boolean;
    formErrorMessage?: string;
    formNameValue?: string;
    formDescriptionValue?: string;
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

class CreateGroupContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    public render(): ReactNode {
        const { cancel, formData } = this.state;
        const { groupState } = this.props;
        const { loading } = groupState;

        if (cancel) {
            return <Redirect to='/' />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else {
            return <CreateGroupFragment
                onCancelButtonClick={this.onCancelButtonClick}
                onFormSubmit={this.onCreateGroupFormSubmit}
                onFormInputChange={this.onFormInputChange}
                formData={formData} />
        }
    }

    private onCreateGroupFormSubmit = () => {
        const { formData } = this.state;
        const { formNameValue, formDescriptionValue } = formData;
        if (!formNameValue || formNameValue.length < 3) {
            this.setState({ formData: { ...formData, formError: true, formErrorMessage: 'Group name must be atleast 3 letters long' } });
        } else {
            this.props.createGroup({ name: formNameValue, description: formDescriptionValue });
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

interface CreateGroupFragmentProps {
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    onFormInputChange: (event: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => void;
    formData: FormData;
};

const CreateGroupFragment: SFC<CreateGroupFragmentProps> = (props) => {
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
                <Header>Create Group</Header>
            </Segment>
            <Segment basic>
                <Form onSubmit={onFormSubmit} error={formError}>
                    <Form.Group>
                        <Form.Input
                            error={formError}
                            width={10}
                            placeholder='Enter group name...'
                            label='Group Name'
                            value={formNameValue}
                            onChange={onFormInputChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.TextArea
                            width={10}
                            placeholder='Enter group description...'
                            label='Group Description'
                            value={formDescriptionValue} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Button
                            primary size='tiny'><Icon name='group' />Create Group</Form.Button>
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
    groupState: state.groupState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    createGroup: (group: CreateGroup) => dispatch(createGroup(group))
});

const ConnectedCreateGroupContainer = connect(mapStateToProps, mapDispatchToProps)(CreateGroupContainer);

export { ConnectedCreateGroupContainer as CreateGroupContainer };