import * as React from 'react';
import { ChangeEventHandler, Component, FunctionComponent, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
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

import {
    ActionType,
    Box,
    BoxState,
    ModifyBox,
    NamedFormData,
    RootState
} from '../../models';
import { findGroupBoxes, updateBox } from '../../state/actions';
import { LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';
import { NotFoundErrorContainer } from '..';

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

interface ComponentState {
    box?: Box;
    cancel: boolean;
    formData: NamedFormData;
}

const initialState: ComponentState = {
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
        let { box } = this.state;
        if (!box) {
            const { boxId } = this.props.match.params;
            const { boxes } = this.props.boxState;
            box = boxes.find((box) => box.id == boxId);
            if (box) {
                const { name, description } = box;
                this.setState({
                    box: box,
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
        const { boxId, groupId } = this.props.match.params;
        const { box, cancel, formData } = this.state;
        const { loading, modified } = this.props.boxState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (modified && modified.actionType == ActionType.UPDATE) {
            return <Redirect to={`/group/${groupId}/box/${boxId}`} />
        } else if (!box) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No box found'
                content={`Could not find box for ID ${boxId}`} />;
        } else {
            return <EditBoxFragment
                box={box}
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
    box: Box;
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    onFormInputChange: (event: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => void;
    onFormTextAreaChange: (event: React.SyntheticEvent<HTMLTextAreaElement>, data: TextAreaProps) => void;
    formData: NamedFormData;
}

const EditBoxFragment: FunctionComponent<EditBoxFragmentProps> = (props) => {
    const {
        box,
        onCancelButtonClick,
        onFormSubmit,
        onFormInputChange,
        onFormTextAreaChange,
        formData
    } = props;
    const { id: boxId, name: boxName, group } = box;
    const { id: groupId, name: groupName } = group;
    const {
        formError,
        formErrorMessage,
        formNameValue,
        formDescriptionValue
    } = formData;

    return (
        <Container>
            <PrimaryHeader />
            <SecondaryHeader>
                <Link to='/'><Icon name='home' /></Link>{'/ '}
                <Link to={`/group/${groupId}`}>{groupName}</Link>{' / '}
                <Link to={`/group/${groupId}/box/${boxId}`}>{boxName}</Link>{' / '}
                <i>Edit Box</i>
            </SecondaryHeader>
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