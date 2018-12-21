import * as React from 'react';
import { ChangeEventHandler, Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
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

import { ModifyBox, BoxState, RootState, ActionType } from '../../models';
import { findGroupBoxes, updateBox } from '../../state/actions';
import { LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    boxState: BoxState;
}

interface ComponentDispatchProps {
    findGroupBoxes: (groupId: number) => Promise<any>;
    updateBox: (boxId: number, box: ModifyBox) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps & RouteProps;

interface FormData {
    formError: boolean;
    formErrorMessage?: string;
    formNameValue: string;
    formDescriptionValue?: string;
}

interface ComponentState {
    boxFound: boolean;
    cancel: boolean;
    formData: FormData;
}

const initialState: ComponentState = {
    boxFound: false,
    cancel: false,
    formData: {
        formError: false,
        formNameValue: ''
    }
};

class EditBoxContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { groupId } = this.props.match.params;
        this.props.findGroupBoxes(groupId);
    }

    public componentDidUpdate() {
        const { boxFound } = this.state;
        if (!boxFound) {
            const { boxId } = this.props.match.params;
            const { boxes } = this.props.boxState;
            const box = boxes.find((box) => box.id == boxId);
            if (box) {
                const { name, description } = box;
                this.setState({
                    boxFound: true,
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
        const { cancel, formData } = this.state;
        const { boxState } = this.props;
        const { loading, modified } = boxState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (modified && modified.actionType == ActionType.UPDATE) {
            const { id: boxId } = modified;
            return <Redirect to={`/group/${groupId}/box/${boxId}`} />
        } else {
            return <EditBoxFragment
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
                    formErrorMessage: 'Box name must be at least 2 letters long'
                }
            });
        } else if (/\s/.test(formNameValue)) {
            this.setState({
                formData: {
                    ...formData,
                    formError: true,
                    formErrorMessage: 'Box name cannot contain any spaces'
                }
            })
        } else {
            const { boxId } = this.props.match.params;
            const box = {
                name: formNameValue,
                description: formDescriptionValue
            };
            this.props.updateBox(boxId, box);
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

interface EditBoxFragmentProps {
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    onFormInputChange: (event: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => void;
    onFormTextAreaChange: (event: React.SyntheticEvent<HTMLTextAreaElement>, data: TextAreaProps) => void;
    formData: FormData;
}

const EditBoxFragment: SFC<EditBoxFragmentProps> = (props) => {
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
            <SecondaryHeader>Edit Box</SecondaryHeader>
            <Segment basic>
                <Form onSubmit={onFormSubmit} error={formError}>
                    <Form.Group>
                        <Form.Input
                            error={formError}
                            width={10}
                            label='Box Name'
                            placeholder='Enter box name...'
                            value={formNameValue}
                            onChange={onFormInputChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.TextArea
                            width={10}
                            label='Box Description'
                            placeholder='Enter box description...'
                            value={formDescriptionValue}
                            onChange={onFormTextAreaChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Button
                            primary size='tiny'><Icon name='cube' />Update Box</Form.Button>
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
    updateBox: (boxId: number, box: ModifyBox) => dispatch(updateBox(boxId, box))
});

const ConnectedEditBoxContainer = connect(mapStateToProps, mapDispatchToProps)(EditBoxContainer);

export { ConnectedEditBoxContainer as EditBoxContainer };