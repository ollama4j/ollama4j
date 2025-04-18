// @site/src/components/Icon.js
import React from 'react';
import { Icon as IconifyIcon } from '@iconify/react';

const IIcon = ({ icon, color, width = '24', height = '24' }) => (
  <IconifyIcon icon={icon} color={color} width={width} height={height} />
);

export default IIcon;