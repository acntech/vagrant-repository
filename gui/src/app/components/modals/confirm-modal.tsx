import * as React from 'react';
import { Component, ReactNode } from 'react';
import {Button, ButtonProps, Icon, Modal, ModalProps} from 'semantic-ui-react'

interface ComponentProps {
    title: string;
    subtitle?: string;
    open: boolean;
    size?: 'fullscreen' | 'large' | 'mini' | 'small' | 'tiny';
    onClose: (event: React.MouseEvent<HTMLElement>, data: ModalProps) => void;
    onClick: (event: React.MouseEvent<HTMLButtonElement>, data: ButtonProps) => void;
}

class ConfirmModalComponent extends Component<ComponentProps> {

    public render(): ReactNode {
        const { open, size, title, subtitle, onClose, onClick } = this.props;

        return (
            <Modal size={size || 'mini'} open={open} onClose={onClose}>
                <Modal.Header>{title}</Modal.Header>
                <Modal.Content>
                    <p>{subtitle}</p>
                </Modal.Content>
                <Modal.Actions>
                    <Button negative onClick={onClick}>
                        <Icon name='delete' /> No
                    </Button>
                    <Button positive onClick={onClick}>
                        <Icon name='checkmark' /> Yes
                    </Button>
                </Modal.Actions>
            </Modal>
        );
    }
}

export { ConfirmModalComponent as ConfirmModal };