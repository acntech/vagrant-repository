import * as React from 'react';
import { ChangeEventHandler, Component, ReactNode, SFC } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { InjectedIntlProps } from 'react-intl';
import {
    Button,
    Container,
    Form, Icon,
    InputOnChangeData,
    TextAreaProps,
    Message,
    Segment
} from 'semantic-ui-react';

import { ModifyVersion, VersionState, RootState, ActionType } from '../../models';
import { findBoxVersions, updateVersion } from '../../state/actions';
import { LoadingIndicator, PrimaryHeader, SecondaryHeader } from '../../components';

interface RouteProps {
    match: any;
}

interface ComponentStateProps {
    versionState: VersionState;
}

interface ComponentDispatchProps {
    findBoxVersions: (boxId: number) => Promise<any>;
    updateVersion: (versionId: number, version: ModifyVersion) => Promise<any>;
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps & InjectedIntlProps & RouteProps;

interface FormData {
    formError: boolean;
    formErrorMessage?: string;
    formNameValue: string;
    formDescriptionValue?: string;
}

interface ComponentState {
    versionFound: boolean;
    cancel: boolean;
    formData: FormData;
}

const initialState: ComponentState = {
    versionFound: false,
    cancel: false,
    formData: {
        formError: false,
        formNameValue: ''
    }
};

class EditVersionContainer extends Component<ComponentProps, ComponentState> {

    constructor(props: ComponentProps) {
        super(props);
        this.state = initialState;
    }

    componentDidMount() {
        const { boxId } = this.props.match.params;
        this.props.findBoxVersions(boxId);
    }

    public componentDidUpdate() {
        const { versionFound } = this.state;
        if (!versionFound) {
            const { versionId } = this.props.match.params;
            const { versions } = this.props.versionState;
            const version = versions.find((version) => version.id == versionId);
            if (version) {
                const { name, description } = version;
                this.setState({
                    versionFound: true,
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
        const { cancel, formData } = this.state;
        const { versionState } = this.props;
        const { loading, modified } = versionState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}/box/${boxId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (modified && modified.actionType == ActionType.UPDATE) {
            const { id: versionId } = modified;
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />
        } else {
            return <EditVersionFragment
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
        if (!formNameValue || formNameValue.length < 1) {
            this.setState({
                formData: {
                    ...formData,
                    formError: true,
                    formErrorMessage: 'Version name must be at least 1 letter long'
                }
            });
        } else if (/\s/.test(formNameValue)) {
            this.setState({
                formData: {
                    ...formData,
                    formError: true,
                    formErrorMessage: 'Version name cannot contain any spaces'
                }
            })
        } else {
            const { versionId } = this.props.match.params;
            const version = {
                name: formNameValue,
                description: formDescriptionValue
            };
            this.props.updateVersion(versionId, version);
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

interface EditVersionFragmentProps {
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    onFormInputChange: (event: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => void;
    onFormTextAreaChange: (event: React.SyntheticEvent<HTMLTextAreaElement>, data: TextAreaProps) => void;
    formData: FormData;
}

const EditVersionFragment: SFC<EditVersionFragmentProps> = (props) => {
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
            <SecondaryHeader>Edit Version</SecondaryHeader>
            <Segment basic>
                <Form onSubmit={onFormSubmit} error={formError}>
                    <Form.Group>
                        <Form.Input
                            error={formError}
                            width={10}
                            label='Version Name'
                            placeholder='Enter version name...'
                            value={formNameValue}
                            onChange={onFormInputChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.TextArea
                            width={10}
                            label='Version Description'
                            placeholder='Enter version description...'
                            value={formDescriptionValue}
                            onChange={onFormTextAreaChange} />
                    </Form.Group>
                    <Form.Group>
                        <Form.Button
                            primary size='tiny'><Icon name='cube' />Update Version</Form.Button>
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
    updateVersion: (versionId: number, version: ModifyVersion) => dispatch(updateVersion(versionId, version))
});

const ConnectedEditVersionContainer = connect(mapStateToProps, mapDispatchToProps)(EditVersionContainer);

export { ConnectedEditVersionContainer as EditVersionContainer };