import * as React from 'react';
import { ChangeEventHandler, Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import {
    Button,
    Container,
    Form,
    Icon,
    InputOnChangeData,
    TextAreaProps,
    Message,
    Segment
} from 'semantic-ui-react';

import {
    ModifyGroup,
    GroupState,
    RootState,
    ActionType
} from '../../models';
import { getGroup, updateGroup } from '../../state/actions';
import { LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';
import { NotFoundErrorContainer } from '..';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    groupState: GroupState;
}

interface ComponentDispatchProps {
    getGroup: (groupId: number) => Promise<any>;
    updateGroup: (groupId: number, group: ModifyGroup) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & RouteProps;

interface FormData {
    formError: boolean;
    formErrorMessage?: string;
    formNameValue: string;
    formDescriptionValue?: string;
}

interface ComponentState {
    groupFound: boolean;
    cancel: boolean;
    formData: FormData;
}

const initialState: ComponentState = {
    groupFound: false,
    cancel: false,
    formData: {
        formError: false,
        formNameValue: ''
    }
};

class EditGroupContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    public componentDidMount() {
        const { groupId } = this.props.match.params;
        this.props.getGroup(groupId);
    }

    public componentDidUpdate() {
        const { groupFound } = this.state;
        if (!groupFound) {
            const { groupId } = this.props.match.params;
            const { groups } = this.props.groupState;
            const group = groups.find((group) => group.id == groupId);
            if (group) {
                const { name, description } = group;
                this.setState({
                    groupFound: true,
                    formData: {
                        formError: false,
                        formNameValue: name,
                        formDescriptionValue: description
                    }
                });
            }
        }
    }

    public render(): ReactNode {
        const { groupId } = this.props.match.params;
        const { groupFound, cancel, formData } = this.state;
        const { groupState } = this.props;
        const { loading, modified } = groupState;

        if (loading) {
            return <LoadingIndicator />;
        } else if (cancel) {
            return <Redirect to={`/group/${groupId}`} />;
        } else if (modified && modified.actionType == ActionType.UPDATE) {
            const { id: modifiedGroupId } = modified;
            return <Redirect to={`/group/${modifiedGroupId}`} />
        } else if (!groupFound) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No group found'
                content={`Could not find group for ID ${groupId}`} />;
        } else {
            return <EditGroupFragment
                onCancelButtonClick={this.onCancelButtonClick}
                onFormSubmit={this.onFormSubmit}
                onFormInputChange={this.onFormInputChange}
                onFormTextAreaChange={this.onFormTextAreaChange}
                formData={formData} />
        }
    }

    private onFormSubmit = () => {
        const { formData } = this.state;
        const { formNameValue, formDescriptionValue } = formData;
        if (!formNameValue || formNameValue.length < 2) {
            this.setState({
                formData: {
                    ...formData,
                    formError: true,
                    formErrorMessage: 'Group name must be at least 2 letters long'
                }
            });
        } else if (/\s/.test(formNameValue)) {
            this.setState({
                formData: {
                    ...formData,
                    formError: true,
                    formErrorMessage: 'Group name cannot contain any spaces'
                }
            })
        } else {
            const { groupId } = this.props.match.params;
            const group = {
                name: formNameValue,
                description: formDescriptionValue
            };
            this.props.updateGroup(groupId, group);
        }
    };

    private onFormInputChange: ChangeEventHandler<HTMLInputElement> = (event) => {
        const { value } = event.currentTarget;
        const { formData } = this.state;
        this.setState({
            formData: {
                ...formData,
                formError: false,
                formWarning: false,
                formNameValue: value
            }
        });
    };

    private onFormTextAreaChange: ChangeEventHandler<HTMLTextAreaElement> = (event) => {
        const { value } = event.currentTarget;
        const { formData } = this.state;
        this.setState({
            formData: {
                ...formData,
                formError: false,
                formWarning: false,
                formDescriptionValue: value
            }
        });
    };

    private onCancelButtonClick = () => {
        this.setState({ cancel: true });
    }
}

interface EditGroupFragmentProps {
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    onFormInputChange: (event: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => void;
    onFormTextAreaChange: (event: React.SyntheticEvent<HTMLTextAreaElement>, data: TextAreaProps) => void;
    formData: FormData;
}

const EditGroupFragment: SFC<EditGroupFragmentProps> = (props) => {
    const {
        onCancelButtonClick,
        onFormSubmit,
        onFormInputChange,
        onFormTextAreaChange,
        formData
    } = props;
    const {
        formError,
        formErrorMessage,
        formNameValue,
        formDescriptionValue
    } = formData;

    return (
        <Container>
            <PrimaryHeader />
            <SecondaryHeader>Edit Group</SecondaryHeader>
            <Segment basic>
                <Form onSubmit={onFormSubmit} error={formError}>
                    <Form.Group>
                        <Form.Input
                            error={formError}
                            width={10}
                            label='Group Name'
                            placeholder='Enter group name...'
                            value={formNameValue}
                            onChange={onFormInputChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.TextArea
                            width={10}
                            label='Group Description'
                            placeholder='Enter group description...'
                            value={formDescriptionValue}
                            onChange={onFormTextAreaChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Button
                            primary size='tiny'><Icon name='group' />Update Group</Form.Button>
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
    getGroup: (groupId: number) => dispatch(getGroup(groupId)),
    updateGroup: (groupId: number, group: ModifyGroup) => dispatch(updateGroup(groupId, group))
});

const ConnectedEditGroupContainer = connect(mapStateToProps, mapDispatchToProps)(EditGroupContainer);

export { ConnectedEditGroupContainer as EditGroupContainer };