import * as React from 'react';
import { ChangeEventHandler, Component, FunctionComponent, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
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

import {
    ActionType,
    ModifyVersion,
    NamedFormData,
    Version,
    VersionState,
    RootState
} from '../../models';
import { findBoxVersions, updateVersion } from '../../state/actions';
import { LoadingIndicator, PrimaryFooter, PrimaryHeader, SecondaryHeader } from '../../components';
import { NotFoundErrorContainer } from '..';

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

interface ComponentState {
    version?: Version;
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
        let { version } = this.state;
        if (!version) {
            const { versionId } = this.props.match.params;
            const { versions } = this.props.versionState;
            version = versions.find((version) => version.id == versionId);
            if (version) {
                const { name, description } = version;
                this.setState({
                    version: version,
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
        const { boxId, groupId, versionId } = this.props.match.params;
        const { cancel, formData, version } = this.state;
        const { loading, modified } = this.props.versionState;

        if (cancel) {
            return <Redirect to={`/group/${groupId}/box/${boxId}`} />;
        } else if (loading) {
            return <LoadingIndicator />;
        } else if (modified && modified.actionType == ActionType.UPDATE) {
            const { id: versionId } = modified;
            return <Redirect to={`/group/${groupId}/box/${boxId}/version/${versionId}`} />
        } else if (!version) {
            return <NotFoundErrorContainer
                header
                icon='warning sign'
                heading='No version found'
                content={`Could not find version for ID ${versionId}`} />;
        } else {
            return <EditVersionFragment
                version={version}
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
    version: Version;
    onCancelButtonClick: () => void;
    onFormSubmit: () => void;
    onFormInputChange: (event: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => void;
    onFormTextAreaChange: (event: React.SyntheticEvent<HTMLTextAreaElement>, data: TextAreaProps) => void;
    formData: NamedFormData;
}

const EditVersionFragment: FunctionComponent<EditVersionFragmentProps> = (props: EditVersionFragmentProps) => {
    const {
        version,
        onCancelButtonClick,
        onFormSubmit,
        onFormInputChange,
        onFormTextAreaChange,
        formData
    } = props;
    const { id: versionId, name: versionName, box } = version;
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
                <Link to={`/group/${groupId}/box/${boxId}/version/${versionId}`}>{versionName}</Link>{' / '}
                <i>Edit Version</i>
            </SecondaryHeader>
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
            <PrimaryFooter />
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